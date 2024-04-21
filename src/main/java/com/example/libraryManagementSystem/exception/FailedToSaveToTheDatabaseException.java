package com.example.libraryManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FailedToSaveToTheDatabaseException extends Exception{
    public FailedToSaveToTheDatabaseException(String message) {
        super(message);
    }
}
