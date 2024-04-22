package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.entity.Patron;

import java.util.List;
import java.util.Optional;

public interface AdminService {
    List<Admin> findAll();
    Optional<Admin> findById(Long id);
    Optional<Admin> findByAccount_Email(String email);
    Optional<Admin> save(Admin admin);
    void deleteById(Long Id);
    void deleteAll();
}
