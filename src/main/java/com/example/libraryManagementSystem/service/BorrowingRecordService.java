package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.entity.BorrowingRecord;
import com.example.libraryManagementSystem.entity.BorrowingRecord.Id;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordService {

    List<BorrowingRecord> findAll();
    Optional<BorrowingRecord> findById(Id id);
    Optional<BorrowingRecord> save(BorrowingRecord  borrowingRecord);
    void deleteAll();
    void deleteById(Id id);
}
