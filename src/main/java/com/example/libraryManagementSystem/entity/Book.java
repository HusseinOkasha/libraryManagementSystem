package com.example.libraryManagementSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "book", uniqueConstraints = {
        @UniqueConstraint(columnNames = "isbn")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author" , nullable = false)
    private String author;

    @Column(name = "publication_year" , nullable = false)
    private String publication_year;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book")
    private Set<BorrowingRecord> borrowingRecord = new HashSet<>();

    public Book() {
    }

    public Book(Long id, String title, String author, String publication_year, String isbn, LocalDateTime createdAt,
                LocalDateTime updatedAt, Set<BorrowingRecord> borrowingRecord) {
        this.title = title;
        this.author = author;
        this.publication_year = publication_year;
        this.isbn = isbn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.borrowingRecord = borrowingRecord;
    }

    public Book(Long id, String title, String author, String publication_year, String isbn) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publication_year = publication_year;
        this.isbn = isbn;
        this.borrowingRecord = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublication_year() {
        return publication_year;
    }

    public void setPublication_year(String publication_year) {
        this.publication_year = publication_year;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<BorrowingRecord> getBorrowingRecord() {
        return borrowingRecord;
    }

    public void setBorrowingRecord(Set<BorrowingRecord> borrowingRecord) {
        this.borrowingRecord = borrowingRecord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return id == book.id && Objects.equals(title, book.title) && Objects.equals(author, book.author) &&
                Objects.equals(publication_year, book.publication_year) && Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(isbn);
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publication_year=" + publication_year +
                ", isbn='" + isbn + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", borrowingRecord=" + borrowingRecord +
                '}';
    }
}
