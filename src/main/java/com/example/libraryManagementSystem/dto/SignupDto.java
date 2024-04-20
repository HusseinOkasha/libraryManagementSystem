package com.example.libraryManagementSystem.dto;


import com.example.libraryManagementSystem.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignupDto(@NotBlank(message = "name must not be empty") String name,
                        @NotBlank(message = "email must not be empty") @Email (message = "email must respect email rules") String email,
                        @NotBlank(message = "phone number must not be empty") String phoneNumber,
                        @NotBlank(message = "phone number must not be empty") String password,
                        @NotNull(message = "Account type must not be empty") AccountType AccountType) {
}
