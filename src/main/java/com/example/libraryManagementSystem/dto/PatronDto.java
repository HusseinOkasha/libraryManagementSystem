package com.example.libraryManagementSystem.dto;

import com.example.libraryManagementSystem.entity.BorrowingRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;

public record PatronDto (Long id, @Valid ProfileDto profileDto, Set<BorrowingRecordDto> borrowingRecordDto) {
}
