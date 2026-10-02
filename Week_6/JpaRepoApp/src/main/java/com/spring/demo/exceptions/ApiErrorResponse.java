package com.spring.demo.exceptions;

public class ApiErrorResponse {

    private int status;
    private String message;
    private long timestamp;

    public ApiErrorResponse(int status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    
}
