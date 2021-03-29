package com.sm.common.error.type;


public enum ErrorType {

    //Validation
    NOT_BLANK("1000", "Please provide the value of %s"),
    PROVIDER_NOT_EMPTY("1001", "Provider must be facebook, google or apple"),
    TOKEN_BAD_REQUEST("1002", "Token is not valid or expire.");


    private final String code;
    private final String message;

    ErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ErrorType getErrorTypeByName(String name){
        return ErrorType.valueOf(name);
    }

}
