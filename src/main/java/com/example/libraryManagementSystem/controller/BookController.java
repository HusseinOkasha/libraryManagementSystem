package com.example.libraryManagementSystem.controller;


import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.entity.Book;
import com.example.libraryManagementSystem.exception.BookAlreadyExistsException;
import com.example.libraryManagementSystem.exception.BookCreationFailureException;
import com.example.libraryManagementSystem.service.BookService;
import com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers.BookMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')") // only admins can add new books
    @PostMapping("/books")
    public BookDto addNewBook(@RequestBody @Valid BookDto bookDto) throws Exception {
        // Check the existence of the book using the ISBN
        Optional<Book> result = bookService.findByISBN(bookDto.isbn());

        // In case the book exists already return 409 conflict
        if(result.isPresent()){
            throw new BookAlreadyExistsException("Book with this isbn already exists");
        }

        // Map the book dto to book entity
        Book bookToBeCreated = BookMapper.bookDtoToBookEntity(bookDto);

        Optional<Book> dbBook;
        try {
            dbBook = bookService.save(bookToBeCreated);
        }
        catch (Exception e){
            // this exception return a response with internal server error
            throw new BookCreationFailureException(e.getMessage());
        }
        return BookMapper.bookEntityToBookDto(dbBook.orElseGet(Book::new));
    }

}
