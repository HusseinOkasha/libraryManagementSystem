package com.example.libraryManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePatronDto(@NotBlank(message = "Name can't be blank")  String name,
                              @NotBlank(message = "phone number can't be blank") String phoneNumber) {
}
