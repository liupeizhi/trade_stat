package com.doorway.tradememo.exception;

/**
 * Note
 * Author:liupz
 * Date:2022/4/5
 */

public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    protected Integer code;
    /**
     * 错误信息
     */
    protected String message;

    public ServiceException() {
        super();
    }

    public ServiceException(BaseErrorInfoInterface errorInfoInterface) {
        super(errorInfoInterface.getMessage());
        this.code = errorInfoInterface.getCode();
        this.message = errorInfoInterface.getMessage();
    }

    public ServiceException(BaseErrorInfoInterface errorInfoInterface, Throwable cause) {
        super(errorInfoInterface.getMessage(), cause);
        this.code = errorInfoInterface.getCode();
        this.message = errorInfoInterface.getMessage();
    }

    public ServiceException(String message) {
        super(message);
        this.message = message;
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ServiceException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}


