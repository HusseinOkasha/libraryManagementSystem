package com.example.libraryManagementSystem.dao;

import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.enums.AccountType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);
    Optional<Account>findByPhoneNumber(String phoneNumber);
    Optional<Account>findByEmailAndAccountType(String email, AccountType accountType);
    @Transactional
    void deleteByEmail(String email);
    @Transactional
    void deleteByPhoneNumber(String phoneNumber);
}
