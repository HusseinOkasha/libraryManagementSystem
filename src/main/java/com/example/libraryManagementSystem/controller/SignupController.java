package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.entity.Patron;
import com.example.libraryManagementSystem.enums.AccountType;
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
    private final PatronService patronService;

    @Autowired
    private final AdminService adminService;

    @Autowired
    private final EncryptionService encryptionService;

    public SignupController(PatronService patronService, AdminService adminService, EncryptionService encryptionService) {

        this.patronService = patronService;
        this.adminService = adminService;
        this.encryptionService = encryptionService;
    }

    @Validated
    @PostMapping("/signup")
    public ProfileDto signup(@RequestBody @Valid SignupDto signupDto) throws Exception {
        // maps signupDto to account entity
        Account account = AccountMapper.signupDtoToAccountEntity(signupDto);

        // encrypt the provided password using Bcrypt before saving it to the database.
        String encryptedPassword = encryptionService.encryptString(signupDto.password());

        // put the encrypted password in the account entity before saving it to the database.
        account.setPassword(encryptedPassword);
        Account dbAccount = null;
        try{
            // db account is the saved account in the database, and it contains the database id

            if(account.getAccountType() == AccountType.ADMIN){
                dbAccount = adminService.save(new Admin(account)).get().getAccount();
            }
            else if(account.getAccountType() == AccountType.PATRON){
                dbAccount = patronService.save(new Patron(account)).get().getAccount();
            }
        }
        catch (DataIntegrityViolationException e){
            throw new AccountCreationFailureException(e.getMessage());
        }

        // maps the account entity to AccountDto
        return AccountMapper.AccountEntityToAccountDto(dbAccount);
    }

}
