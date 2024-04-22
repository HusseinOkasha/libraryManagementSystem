package com.example.libraryManagementSystem.service;

import com.example.libraryManagementSystem.dao.AdminRepository;
import com.example.libraryManagementSystem.entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements  AdminService{
    @Autowired
    private final AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Optional<Admin> findById(Long id) {

        return adminRepository.findById(id);
    }

    @Override
    public Optional<Admin> findByAccount_Email(String email) {
        return adminRepository.findByAccount_Email(email);
    }

    @Override
    public Optional<Admin> save(Admin admin) {
        return Optional.of(adminRepository.save(admin));
    }

    @Override
    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        adminRepository.deleteAll();
    }
}
