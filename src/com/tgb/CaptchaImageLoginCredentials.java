package com.tgb;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jasig.cas.authentication.principal.RememberMeUsernamePasswordCredentials;
/**
 * 改造的org.jasig.cas.authentication.principal.UsernamePasswordCredentials.java，
 * 你把源码拿出来和这个类对比一下就知道只是增加了一个private String code;
 * @author a
 *
 */
public class CaptchaImageLoginCredentials extends RememberMeUsernamePasswordCredentials {
    
    private static final long serialVersionUID = 1L;
    
    
    /** The code. */
    @NotNull
    @Size(min=1,message = "验证码为空")
    private String code;
    
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code ) {
        this.code = code;
    }
    
}
