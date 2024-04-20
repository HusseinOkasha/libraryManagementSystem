package com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers;

import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.dto.BookDetailsDto;
import com.example.libraryManagementSystem.entity.Book;

public class BookMapper {

    // to/from BookDto
    public static Book bookDtoToBookEntity(BookDto bookDto){
        BookDetailsDto bookDetailsDto = bookDto.bookDetailsDto();
        return new Book(bookDto.id(),bookDetailsDto.title(), bookDetailsDto.author(), bookDetailsDto.publication_year(),
                bookDetailsDto.isbn(), bookDto.createdAt(), bookDto.updatedAt(), null);
    }
    public static BookDto bookEntityToBookDto(Book book){
        return new BookDto(book.getId(),
                new BookDetailsDto(book.getTitle(), book.getAuthor(), book.getPublication_year(), book.getIsbn()),
                book.getCreatedAt(), book.getUpdatedAt());
    }

    // from createBookDto
    public static Book bookDetailsDtoToBookEntity(BookDetailsDto dto){
        return new Book((long)0, dto.title(), dto.author(), dto.publication_year(),dto.isbn());
    }

}
