package com.example.libraryManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BookReturnException extends Exception{
    public BookReturnException(String message) {
        super(message);
    }
}
