package com.tgb.sourceAnalysis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.Credentials;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.bind.CredentialsBinder;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.execution.RequestContext;

/**
 * Action to authenticate credentials and retrieve a TicketGrantingTicket for
 * those credentials. If there is a request for renew, then it also generates
 * the Service Ticket required.
 * 
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 3.0.4
 */
public class AuthenticationViaFormAction {
    
    /**
     * Binder that allows additional binding of form object beyond Spring
     * defaults.
     */
    private CredentialsBinder credentialsBinder;
    
    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;
    
    @NotNull
    private CookieGenerator warnCookieGenerator;
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    public final void doBind(final RequestContext context, final Credentials credentials) throws Exception {
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
        
        if (this.credentialsBinder != null && this.credentialsBinder.supports(credentials.getClass())) {
            this.credentialsBinder.bind(request, credentials);
        }
    }
    
    public final String submit(final RequestContext context, final Credentials credentials, final MessageContext messageContext) throws Exception {
        // Validate login ticket
        //从FlowScope域中获取lt
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        //从request域中获取lt
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        //判断这2个域中的lt值是否相等
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            //不相等执行的方法
            this.logger.warn("Invalid login ticket " + providedLoginTicket);
            final String code = "INVALID_TICKET";
            messageContext.addMessage(
                    new MessageBuilder().error().code(code).arg(providedLoginTicket).defaultText(code).build());
            return "error";
        }
        //lt验证通过后
        //获取TGT
        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
        //这个service是对应的服务。就是在你访问某个应用程序的时候，
        //该请求被统一认证的过滤器拦截之后跳转到统一认证进行认证。
        //跳转的过程中，会把你之前的请求的路径记录下来并作为参数传递到统一认证。
        //在统一认证系统中配置的所有可以使用统一认证系统的业务系统的配置信息。
        //该认证请求到了统一认证之后，统一认证根据这个路径查找对应的系统的配置信息。详情参照系统管理部分。
        final Service service = WebUtils.getService(context);
        //StringUtils.hasText("字符串"),字符串中为null,""," ",返回false,否则返回true
        if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null && service != null) {
            
            try {
                //生成ST
                final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(ticketGrantingTicketId, service, credentials);
                //将ST放入到request域中.key为"serviceTicketId",value为serviceTicketId(上一步生成的).
                WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
                putWarnCookieIfRequestParameterPresent(context);
                return "warn";
            } catch (final TicketException e) {
                if (isCauseAuthenticationException(e)) {
                    populateErrorsInstance(e, messageContext);
                    return getAuthenticationExceptionEventId(e);
                }
                
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
                if (logger.isDebugEnabled()) {
                    logger.debug("Attempted to generate a ServiceTicket using renew=true with different credentials", e);
                }
            }
        }
        
        try {
            //如果不存在TGT，则通过下面方法新生成一个TGT。放到request域中
            WebUtils.putTicketGrantingTicketInRequestScope(context, this.centralAuthenticationService.createTicketGrantingTicket(credentials));
            putWarnCookieIfRequestParameterPresent(context);
            return "success";
        } catch (final TicketException e) {
            populateErrorsInstance(e, messageContext);
            if (isCauseAuthenticationException(e)){
                return getAuthenticationExceptionEventId(e);
            }
            return "error";
        }
    }
    
    
    private void populateErrorsInstance(final TicketException e, final MessageContext messageContext) {
        
        try {
            messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).defaultText(e.getCode()).build());
        } catch (final Exception fe) {
            logger.error(fe.getMessage(), fe);
        }
    }
    
    private void putWarnCookieIfRequestParameterPresent(final RequestContext context) {
        final HttpServletResponse response = WebUtils.getHttpServletResponse(context);
        
        if (StringUtils.hasText(context.getExternalContext().getRequestParameterMap().get("warn"))) {
            this.warnCookieGenerator.addCookie(response, "true");
        } else {
            this.warnCookieGenerator.removeCookie(response);
        }
    }
    
    private AuthenticationException getAuthenticationExceptionAsCause(final TicketException e) {
        return (AuthenticationException) e.getCause();
    }
    
    private String getAuthenticationExceptionEventId(final TicketException e) {
        final AuthenticationException authEx = getAuthenticationExceptionAsCause(e);
        
        if (this.logger.isDebugEnabled()){
            this.logger.debug("An authentication error has occurred. Returning the event id " + authEx.getType());
        }
        
        return authEx.getType();
    }
    
    private boolean isCauseAuthenticationException(final TicketException e) {
        return e.getCause() != null && AuthenticationException.class.isAssignableFrom(e.getCause().getClass());
    }
    
    public final void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }
    
    /**
     * Set a CredentialsBinder for additional binding of the HttpServletRequest
     * to the Credentials instance, beyond our default binding of the
     * Credentials as a Form Object in Spring WebMVC parlance. By the time we
     * invoke this CredentialsBinder, we have already engaged in default binding
     * such that for each HttpServletRequest parameter, if there was a JavaBean
     * property of the Credentials implementation of the same name, we have set
     * that property to be the value of the corresponding request parameter.
     * This CredentialsBinder plugin point exists to allow consideration of
     * things other than HttpServletRequest parameters in populating the
     * Credentials (or more sophisticated consideration of the
     * HttpServletRequest parameters).
     *
     * @param credentialsBinder the credentials binder to set.
     */
    public final void setCredentialsBinder(final CredentialsBinder credentialsBinder) {
        this.credentialsBinder = credentialsBinder;
    }
    
    public final void setWarnCookieGenerator(final CookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }
}