package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dao.BorrowingRecordRepository;
import com.example.libraryManagementSystem.entity.BorrowingRecord;
import com.example.libraryManagementSystem.entity.BorrowingRecord.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowingRecordImpl implements BorrowingRecordService{
    @Autowired
    private final BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingRecordImpl(BorrowingRecordRepository borrowingRecordRepository) {
        this.borrowingRecordRepository = borrowingRecordRepository;
    }


    @Override
    public List<BorrowingRecord> findAll() {
        return borrowingRecordRepository.findAll();
    }

    @Override
    public Optional<BorrowingRecord> findById(BorrowingRecord.Id id) {
        return borrowingRecordRepository.findById(id);
    }

    @Override
    public Optional<BorrowingRecord> save(BorrowingRecord borrowingRecord) {
        return Optional.of(borrowingRecordRepository.save(borrowingRecord));
    }

    @Override
    public void deleteById(Id id) {
        borrowingRecordRepository.deleteById(id);
    }
}
