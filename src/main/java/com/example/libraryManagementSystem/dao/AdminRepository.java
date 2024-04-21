package com.example.libraryManagementSystem.dao;


import com.example.libraryManagementSystem.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
