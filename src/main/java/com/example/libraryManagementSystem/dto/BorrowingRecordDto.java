package com.example.libraryManagementSystem.dto;

import com.example.libraryManagementSystem.entity.BorrowingRecord.Id;
import com.example.libraryManagementSystem.enums.BookStatus;
import jakarta.validation.constraints.NotNull;

public record BorrowingRecordDto(@NotNull Long patronId,  @NotNull Long bookId , @NotNull BookStatus bookStatus) {}
