package com.aem.rnd.lab.core.models;

public class ErrorModel {

    private String status;

    private String errorMsg;
    private static final String[] schemas = new String[]{
            "Shemas for SCIMS in array return"
    };

    // Constructor
    public ErrorModel(String status, String errorMsg) {
        this.status = status;
        this.errorMsg = errorMsg;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String[] getSchemas() {
        return schemas;
    }
}
