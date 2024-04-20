package com.example.libraryManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends Exception {
    public  BookAlreadyExistsException(String message){
        super(message);
    }
}
