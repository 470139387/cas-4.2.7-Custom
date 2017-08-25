package com.tgb.handler;

import javax.validation.constraints.NotNull;

import org.jasig.cas.adaptors.jdbc.AbstractJdbcUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.handler.AuthenticationException;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;
import org.springframework.dao.IncorrectResultSizeDataAccessException;


public class CustomQueryDBHandler extends AbstractJdbcUsernamePasswordAuthenticationHandler{
    
    @NotNull
    private String sql;
    
    @SuppressWarnings("deprecation" )
    @Override
    protected final boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) throws AuthenticationException {
        //前台输入的用户名和密码
        final String username = getPrincipalNameTransformer().transform(credentials.getUsername());
        final String password = credentials.getPassword();
        
        System.err.println("password:"+password);
        final String encryptedPassword = this.getPasswordEncoder().encode(
                password);
        System.err.println("encryptedPassword:"+encryptedPassword);
        try {
            
            /**
             * 写自己的验证逻辑
             */
            System.err.println("用户输入的password:"+password);
            
            final String dbPassword = getJdbcTemplate().queryForObject(this.sql, String.class, username);;
            /*if (dbPassword == null || dbPassword == "") {
                throw new BadPasswordAuthenticationException();
            }*/
            System.err.println("dbPassword:"+dbPassword);
            boolean i =   dbPassword.equals(password);
            return i;
            
        } catch (final IncorrectResultSizeDataAccessException e) {
            // this means the username was not found.
            return false;
        }
    }
    
    /**
     * @param sql The sql to set.
     */
    public void setSql(final String sql) {
        this.sql = sql;
    }
    
}
