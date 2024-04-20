package com.example.libraryManagementSystem.dao;

import com.example.libraryManagementSystem.entity.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatronRepository extends JpaRepository<Patron, Long> {
}
