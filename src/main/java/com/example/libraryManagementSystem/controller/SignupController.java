package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.entity.Patron;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.exception.AccountAlreadyExistsException;
import com.example.libraryManagementSystem.exception.AccountCreationFailureException;
import com.example.libraryManagementSystem.security.EncryptionService;
import com.example.libraryManagementSystem.service.AccountService;
import com.example.libraryManagementSystem.service.AdminService;
import com.example.libraryManagementSystem.service.PatronService;
import com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers.AccountMapper;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SignupController {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final AdminService adminService;

    @Autowired
    private final EncryptionService encryptionService;

    public SignupController(AccountService accountService, AdminService adminService, EncryptionService encryptionService) {
        this.accountService = accountService;
        this.adminService = adminService;
        this.encryptionService = encryptionService;
    }

    @Validated
    @PostMapping("/signup")
    public ProfileDto signup(@RequestBody @Valid SignupDto signupDto) throws Exception {
        // check if there is admin with this email
        Optional<Account> result  = accountService.findByEmailAndAccountType(signupDto.email(), AccountType.ADMIN);

        // throw exception with response status code 409 conflict.
        if(result.isPresent()){
            throw new AccountAlreadyExistsException("this email already exists");
        }
        
        // create new account.
        Account account  = AccountMapper.signupDtoToAccountEntity(signupDto);

        //Encrypt the provided raw password.
        String encryptedPassword = encryptionService.encryptString(signupDto.password());

        // create new admin
        Admin admin = new Admin(account, encryptedPassword);

        // persist the admin to the database.
        Optional<Admin> adminSaveResult = adminService.save(admin);

        Admin dbAdmin = adminSaveResult.orElseThrow(
                ()-> new AccountCreationFailureException("Error saving to the database"));
        return AccountMapper.accountEntityToProfileDto(dbAdmin.getAccount());
    }

}
