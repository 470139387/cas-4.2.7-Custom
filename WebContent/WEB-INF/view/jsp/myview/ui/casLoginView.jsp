<jsp:directive.include file="includes/top.jsp" />

  <div class="box fl-panel" id="login">
			<form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
                  <form:errors path="*" id="msg" cssClass="errors" element="div" />
                <!-- <spring:message code="screen.welcome.welcome" /> -->
                    
                    <!-- 提示信息'请输入您的用户名和密码' -->
                    <h2><spring:message code="screen.welcome.instructions" /></h2>
                    
                    <!-- 用户名: -->
                    <div class="row fl-controls-left">
                        <label for="username" class="fl-label"><spring:message code="screen.welcome.label.netid" /></label>
						<c:if test="${not empty sessionScope.openIdLocalId}">
						<strong>${sessionScope.openIdLocalId}</strong>
						<input type="hidden" id="username" name="username" value="${sessionScope.openIdLocalId}" />
						</c:if>

						<c:if test="${empty sessionScope.openIdLocalId}">
						<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
						<form:input cssClass="required" cssErrorClass="error" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
						</c:if>
                    </div>
                    
                    <!-- 密码: -->
                    <div class="row fl-controls-left">
                        <label for="password" class="fl-label"><spring:message code="screen.welcome.label.password" /></label>
						<%--
						NOTE: Certain browsers will offer the option of caching passwords for a user.  There is a non-standard attribute,
						"autocomplete" that when set to "off" will tell certain browsers not to prompt to cache credentials.  For more
						information, see the following web page:
						http://www.geocities.com/technofundo/tech/web/ie_autocomplete.html
						--%>
						<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
						<form:password cssClass="required" cssErrorClass="error" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
                    </div>
                   
                    <!-- 验证码 -->  
                    <div class="row fl-controls-left">  
                        <label for="code" class="fl-label"><spring:message code="screen.welcome.label.code" /></label>  
                               
                          
                        <input class="required" type="text" tabindex="3"  id="code" size="10"   name="code"  autocomplete="off"  style="float:left;"/>  
                               
                        <div style="height:30px;width:150px;text-align:center;margin-left:5px; float:left;vertical-align:middle; display: table-cell;">  
                        <a href="javascript:refresh();" onclick="refresh();"  style="width:130px;height:30px;">  
                               
                        <span style="display: block;float:left;width:60px;height:25px;float:left;">  
                            <img id="vali"  width="60" height="30" src="captcha.htm" style="padding-left: 5px;" />  
                        </span>  
                         <span style="display:block;width:60px;height:100%;float:left;vertical-align:middle; display: table-cell;margin-left:15px;"><spring:message code="screen.welcome.label.prompts" /></span>  
                        </a>  
                    </div> 
                   
                    
                    <div class="row btn-row">
						<input type="hidden" name="lt" value="${loginTicket}" />
						<input type="hidden" name="execution" value="${flowExecutionKey}" />
						<input type="hidden" name="_eventId" value="submit" />
                        <!-- 登录按钮 -->
                        <input class="btn-submit" name="submit" accesskey="l" value="<spring:message code="screen.welcome.button.login" />" tabindex="4" type="submit" />
                        <!-- 重置按钮 -->
                        <input class="btn-reset" name="reset" accesskey="c" value="<spring:message code="screen.welcome.button.clear" />" tabindex="5" type="reset" />
                    </div>
            </form:form>
          </div>
            <%-- <div id="sidebar">
				<div class="sidebar-content">
                <p class="fl-panel fl-note fl-bevel-white fl-font-size-80"><spring:message code="screen.welcome.security" /></p>
				</div>
            </div> --%>
            <script type="text/javascript">  
                    function refresh(){  
                    	document.getElementById( "vali" ).src="";
                    	//加入时间戳参数,否则浏览器会缓存.导致刷新无效
                    	document.getElementById( "vali" ).src="captcha.htm?t="+new Date().getTime();
                           //fm1.vali.src="";  
                           //fm1.vali.src="captcha.htm";  
                           //img.src='captcha.htm?t='+new Date().getTime()  
                     }  
            </script>
