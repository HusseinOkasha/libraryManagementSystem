package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.entity.Patron;

import java.util.List;
import java.util.Optional;


public interface PatronService {
    List<Patron> findAll();
    Optional<Patron> findById(Long id);
    Optional<Patron> save(Patron patron);
    void deleteAll();
    void deleteById(Long Id);
}
