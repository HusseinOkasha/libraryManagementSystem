package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAll();
    Optional<Book>findById(long id);
    Optional<Book>findByISBN(String isbn);
    Optional<Book>save(Book book);
    void deleteAll();
    void deleteById(Long id);
}
