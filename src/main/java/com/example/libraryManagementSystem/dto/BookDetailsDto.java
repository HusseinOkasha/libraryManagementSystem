package com.example.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// represents the request body of add new book request
public record BookDetailsDto(@NotBlank(message = "title can't be empty")  String title,
                             @NotBlank(message = "Author can't be empty") String author,
                             @NotNull(message = "publication year can't be empty") String publication_year,
                             @NotBlank(message = "isbn can't be empty")  String isbn) {
}
