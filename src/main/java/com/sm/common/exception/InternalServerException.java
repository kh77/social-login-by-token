package com.sm.common.exception;


public class InternalServerException extends RuntimeException {
    private String code;
    private String message;
    private Throwable cause;

    public InternalServerException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public InternalServerException(String code , String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.cause = cause;
    }

    public InternalServerException( Throwable cause) {
        super(cause);
        this.cause = cause;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
