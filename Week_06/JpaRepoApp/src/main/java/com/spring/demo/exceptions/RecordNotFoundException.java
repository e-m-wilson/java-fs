package com.spring.demo.exceptions;

public class RecordNotFoundException extends RuntimeException {

    public RecordNotFoundException(String message) {
        super(message);
    }

    public RecordNotFoundException(Throwable cause) {
        super(cause);
    }

    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
