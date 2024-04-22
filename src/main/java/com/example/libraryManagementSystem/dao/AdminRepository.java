package com.example.libraryManagementSystem.dao;


import com.example.libraryManagementSystem.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAccount_Email(String email);
}
