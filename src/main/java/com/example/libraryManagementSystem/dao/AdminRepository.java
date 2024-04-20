package com.example.libraryManagementSystem.dao;

import com.example.libraryManagementSystem.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Account, Long> {
}
