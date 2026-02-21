package com.mint.bytelink.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timeStamp;

    public ErrorResponse(int status , String message){
        this.status = status;
        this.message = message;
        timeStamp = LocalDateTime.now();
    }
}
