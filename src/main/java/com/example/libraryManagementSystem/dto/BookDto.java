package com.example.libraryManagementSystem.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

// represents the book entity when we return it to the outside world.
public record BookDto (@NotBlank Long id, @Valid BookDetailsDto bookDetailsDto, LocalDateTime createdAt,
                       LocalDateTime updatedAt){
}
