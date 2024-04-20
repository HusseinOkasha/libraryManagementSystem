package com.example.libraryManagementSystem.dao;

import com.example.libraryManagementSystem.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.libraryManagementSystem.entity.BorrowingRecord.Id;
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Id> {
}
