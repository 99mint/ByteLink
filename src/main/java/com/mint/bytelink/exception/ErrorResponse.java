package com.mint.bytelink.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status , String message){
        this.status = status;
        this.message = message;
    }
}
