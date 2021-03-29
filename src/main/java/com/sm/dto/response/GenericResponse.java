package com.sm.dto.response;


import java.io.Serializable;

public class GenericResponse implements Serializable {

    public static final String REQUEST_SUCCESSFUL_MESSAGE = "Request completed successfully";
    private static final long serialVersionUID = 6835192601898364280L;

    private String message = REQUEST_SUCCESSFUL_MESSAGE;
    private Object data;

    public GenericResponse(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
