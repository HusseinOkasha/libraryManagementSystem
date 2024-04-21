package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.entity.Book;
import com.example.libraryManagementSystem.entity.BorrowingRecord.Id;
import com.example.libraryManagementSystem.entity.BorrowingRecord;
import com.example.libraryManagementSystem.entity.Patron;
import com.example.libraryManagementSystem.enums.BookStatus;
import com.example.libraryManagementSystem.exception.BookAlreadyBorrowedByPatronException;
import com.example.libraryManagementSystem.exception.BookNotFoundException;
import com.example.libraryManagementSystem.exception.BookReturnException;
import com.example.libraryManagementSystem.exception.PatronNotFoundException;
import com.example.libraryManagementSystem.service.BookService;
import com.example.libraryManagementSystem.service.BorrowingRecordService;
import com.example.libraryManagementSystem.service.PatronService;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BorrowAndReturnController {
    @Autowired
    private final BorrowingRecordService borrowingRecordService;

    @Autowired
    private final BookService bookService;

    @Autowired
    private final PatronService patronService;

    public BorrowAndReturnController(BorrowingRecordService borrowingRecordService, BookService bookService, PatronService patronService) {
        this.borrowingRecordService = borrowingRecordService;
        this.bookService = bookService;
        this.patronService = patronService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public void borrowBook(@PathVariable @PositiveOrZero Long bookId, @PathVariable @PositiveOrZero Long  patronId)
            throws Exception {

        // Check if this patron has borrowed this book previously
        Optional<BorrowingRecord> borrowingRecordFetchResult = borrowingRecordService.findById(new Id(patronId, bookId));

        // in case the record already exists in the database.
        if(borrowingRecordFetchResult.isPresent()){
            BorrowingRecord dbBorrowingRecord = borrowingRecordFetchResult.get();
            // if the book is currently borrowed by the same patron.
            if(dbBorrowingRecord.getStatus() == BookStatus.BORROWED){
                throw new BookAlreadyBorrowedByPatronException(
                        "patron with id:" + patronId + " has already borrowed book with id " + bookId);
            }
            else{
                // if he had borrowed the book then returned it.
                dbBorrowingRecord.setStatus(BookStatus.BORROWED);
                borrowingRecordService.save(dbBorrowingRecord);
                return;
            }
        }

        // fetch the book
        Optional<Book> fetchBookResult = bookService.findById(bookId);

        // if the book id doesn't exist through book not found exception.
        Book dbbook = fetchBookResult.orElseThrow(()-> new BookNotFoundException("there is no book with id: "+ bookId));

        // fetch the patron
        Optional<Patron> fetchPatronResult = patronService.findById(patronId);

        // if the patron doesn't exist through patron not found exception.
        Patron dbpatron = fetchPatronResult.orElseThrow(
                ()-> new PatronNotFoundException("there is no patron with id:" + patronId));

        // create borrowingRecord
        Id id = new Id(dbpatron.getId(), dbbook.getId());
        BorrowingRecord newBorrowingRecord = new BorrowingRecord(id, dbpatron, dbbook, BookStatus.BORROWED, null, null);

        borrowingRecordService.save(newBorrowingRecord);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @PutMapping("/borrow/{bookId}/patron/{patronId}")
    public void returnBook(@PathVariable @PositiveOrZero Long bookId, @PathVariable @PositiveOrZero Long  patronId)
            throws Exception {
        // fetch the borrowing record
        Optional<BorrowingRecord> borrowingRecordFetchResult = borrowingRecordService.findById(new Id(patronId, bookId));

        // if the borrowing record throw bookReturnException

        BorrowingRecord dbBorrowingRecord = borrowingRecordFetchResult.orElseThrow(
                ()->new BookReturnException("patron with id:" + patronId + " didn't borrow book with id: " + bookId ));

        // update status to Returned
        dbBorrowingRecord.setStatus(BookStatus.RETURNED);

        borrowingRecordService.save(dbBorrowingRecord);
    }



}
