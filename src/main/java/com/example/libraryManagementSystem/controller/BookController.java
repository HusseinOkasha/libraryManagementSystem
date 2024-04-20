package com.example.libraryManagementSystem.controller;


import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.dto.BookDetailsDto;
import com.example.libraryManagementSystem.entity.Book;
import com.example.libraryManagementSystem.exception.BookAlreadyExistsException;
import com.example.libraryManagementSystem.exception.BookCreationFailureException;
import com.example.libraryManagementSystem.exception.BookNotFoundException;
import com.example.libraryManagementSystem.service.BookService;
import com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers.BookMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public BookDto addNewBook(@RequestBody @Valid BookDetailsDto bookDetailsDto) throws Exception {
        // Check the existence of the book using the ISBN
        Optional<Book> result = bookService.findByISBN(bookDetailsDto.isbn());

        // In case the book exists already return 409 conflict
        if(result.isPresent()){
            throw new BookAlreadyExistsException("Book with this isbn already exists");
        }

        // Map the book dto to book entity
        Book bookToBeCreated = BookMapper.bookDetailsDtoToBookEntity(bookDetailsDto);

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

    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_PATRON')")
    @GetMapping("/books")
    public List<BookDto> getAllBooks(){
        List<Book> result = bookService.findAll();
        return result.stream().map(BookMapper::bookEntityToBookDto).toList();
    }

    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or hasAuthority('SCOPE_PATRON')")
    @GetMapping("/books/{id}")
    public BookDto getBookById(@PathVariable @PositiveOrZero Long id){
        // fetch the book from the database
        Optional<Book> result = bookService.findById(id);

        // map it to a bookDto and return it.
        return BookMapper.bookEntityToBookDto(result.orElseGet(Book::new));
    }


    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @PutMapping("/books/{id}")
    public BookDto updateBookById(@PathVariable @PositiveOrZero Long id, @Valid @RequestBody BookDetailsDto bookDetailsDto)
            throws BookNotFoundException {
        // fetch the book from the database
        Optional<Book> result = bookService.findById(id);
        Book dbBook = result.orElseThrow(()->new BookNotFoundException("there is no book with id " + id));

        // reflect the updates to the database book
        dbBook.setTitle(bookDetailsDto.title());
        dbBook.setAuthor(bookDetailsDto.author());
        dbBook.setPublication_year(bookDetailsDto.publication_year());
        dbBook.setIsbn(bookDetailsDto.isbn());

        // save it to the database
        result = bookService.save(dbBook);
        return BookMapper.bookEntityToBookDto(result.orElseGet(Book::new));
    }

    @Validated
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @DeleteMapping("/books/{id}")
    public void deleteBookById(@PathVariable @PositiveOrZero Long id){
        bookService.deleteById(id);
    }



}
