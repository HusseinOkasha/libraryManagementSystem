package com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers;

import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.entity.Book;

public class BookMapper {

    public static Book bookDtoToBookEntity(BookDto bookDto){
        return new Book(bookDto.title(), bookDto.author(), bookDto.publication_year(), bookDto.isbn());
    }
    public static BookDto bookEntityToBookDto(Book book){
        return new BookDto(book.getTitle(), book.getAuthor(), book.getPublication_year(), book.getIsbn());
    }
}
