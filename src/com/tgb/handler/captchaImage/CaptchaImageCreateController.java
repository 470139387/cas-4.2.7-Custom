package com.tgb.handler.captchaImage;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.tgb.util.ValidatorCodeUtil;
import com.tgb.util.ValidatorCodeUtil.ValidatorCode;

public class CaptchaImageCreateController implements Controller, InitializingBean{
    
    @Override
    public void afterPropertiesSet() throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest request , HttpServletResponse response ) throws Exception {
        ValidatorCode codeUtil = ValidatorCodeUtil.getCode();
        System.out.println("code="+codeUtil.getCode());
        
        request.getSession().setAttribute("code", codeUtil.getCode());
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        
        ServletOutputStream sos = null;
        try {
            // 将图像输出到Servlet输出流中。
            sos = response.getOutputStream();
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(sos);
            encoder.encode(codeUtil.getImage());
            sos.flush();
            sos.close();
        } catch (Exception e) {
        } finally {
            if (null != sos) {
                try {
                    sos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
}
