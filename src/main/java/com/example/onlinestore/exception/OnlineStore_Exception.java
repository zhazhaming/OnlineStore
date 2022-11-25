package com.example.onlinestore.exception;

/**
 * 统一描述异常
 */
public class OnlineStore_Exception extends RuntimeException {
    private final Integer code;
    private final String message;

    public OnlineStore_Exception(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public OnlineStore_Exception(OnlineStore_ExceptionEnum exceptionEnum){
        this(exceptionEnum.getCode (),exceptionEnum.getMsg ());
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
