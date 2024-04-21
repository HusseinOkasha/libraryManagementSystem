package com.example.libraryManagementSystem.entity;

import com.example.libraryManagementSystem.enums.BookStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "borrowing_record")
public class BorrowingRecord {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "patron_id")
        private Long patronId;

        @Column(name = "book_id")
        private Long bookId;
        public Id() {
        }

        public Id(Long patronId, Long bookId) {
            this.patronId = patronId;
            this.bookId = bookId;
        }

    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne()
    @JoinColumn(name = "patron_id", insertable = false, updatable = false)
    private Patron patron;

    @ManyToOne()
    @JoinColumn(name = "book_id", insertable = false, updatable = false)
    private Book book;

    @Column(name = "status", nullable = false)
    private BookStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BorrowingRecord() {
    }

    public BorrowingRecord(Id id, Patron patron, Book book, BookStatus status, LocalDateTime createdAt,
                           LocalDateTime updatedAt) {
        this.id = id;
        this.patron = patron;
        this.book = book;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public Patron getPatron() {
        return patron;
    }

    public void setPatron(Patron patron) {
        this.patron = patron;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BorrowingRecord that)) return false;
        return Objects.equals(patron, that.patron) && Objects.equals(book, that.book);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patron, book);
    }

    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "id=" + id +
                ", patron=" + patron +
                ", book=" + book +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
