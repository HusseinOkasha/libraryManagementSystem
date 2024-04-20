package com.example.libraryManagementSystem.dto;

import com.example.libraryManagementSystem.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record ProfileDto(@NotBlank(message = "name must not be empty") String name,
                         @NotBlank(message = "email must not be empty") @Email(message = "email must respect email rules") String email,
                         @NotBlank(message = "phone number must not be empty") String phoneNumber,
                         @NotBlank(message = "Account type must not be empty") AccountType AccountType,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {

}
