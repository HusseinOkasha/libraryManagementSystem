package com.example.libraryManagementSystem.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name ="patron")
public class Patron {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long  id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "patron")
    private Set<BorrowingRecord> borrowingRecord = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    public Patron() {
    }

    public Patron(Account account, Set<BorrowingRecord> borrowingRecord, LocalDateTime createdAt,
                  LocalDateTime updatedAt) {
        this.account = account;
        this.borrowingRecord = borrowingRecord;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
        if (!(o instanceof Patron patron)) return false;
        return Objects.equals(account, patron.account);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(account);
    }

    @Override
    public String toString() {
        return "Patron{" +
                "id=" + id +
                ", account=" + account +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
