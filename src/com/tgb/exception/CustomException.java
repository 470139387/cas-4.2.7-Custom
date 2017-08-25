package com.tgb.exception;

public class CustomException extends Exception{
    private static final long serialVersionUID = 669408440571941612L;
    //异常信息
    private String message;
    
    public CustomException(String message){
        super(message);
        this.message = message;
        
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
