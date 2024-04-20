package com.example.libraryManagementSystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BookCreationFailureException extends Exception {
    public BookCreationFailureException(String message) {
        super(message);
    }
}
