package com.kw.myproject.model;

import org.springframework.http.HttpStatus;


public class ResponseModel {
    private int status;
    private String message;
    private Object data;

    public ResponseModel() {
    }

    public ResponseModel(Object data) {
        this.status = HttpStatus.OK.value();
        this.message = "Success";
        this.data = data;
    }

    public ResponseModel(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public ResponseModel(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

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
