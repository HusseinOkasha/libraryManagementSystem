package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.enums.AccountType;


import java.util.List;
import java.util.Optional;

public interface AccountService {
    List<Account> findAll();
    Optional<Account> findById(long id);
    Optional<Account>findByEmail(String email);
    Optional<Account>findByPhoneNumber(String phoneNumber);
    Optional<Account>findByEmailAndAccountType(String email, AccountType accountType);
    Optional<Account> save(Account account);
    void deleteById(long Id);
    void deleteByEmail(String email);
    void deleteByPhoneNumber(String phoneNumber);


}
