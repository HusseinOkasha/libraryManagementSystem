package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dao.PatronRepository;
import com.example.libraryManagementSystem.entity.Patron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatronServiceImpl implements PatronService{
    @Autowired
    private final PatronRepository patronRepository;

    public PatronServiceImpl(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }


    @Override
    public List<Patron> findAll() {
        return patronRepository.findAll();
    }

    @Override
    public Optional<Patron> findById(Long id) {
        return patronRepository.findById(id);
    }

    @Override
    public Optional<Patron> save(Patron patron) {
        return Optional.of(patronRepository.save(patron));
    }

    @Override
    public void deleteById(Long id) {
        patronRepository.deleteById(id);
    }
}
