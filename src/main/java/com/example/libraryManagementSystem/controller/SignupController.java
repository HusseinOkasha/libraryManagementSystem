package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.exception.AccountCreationFailureException;
import com.example.libraryManagementSystem.security.EncryptionService;
import com.example.libraryManagementSystem.service.AccountService;
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
    private final EncryptionService encryptionService;

    public SignupController(AccountService accountService, EncryptionService encryptionService) {
        this.accountService = accountService;
        this.encryptionService = encryptionService;
    }

    @Validated
    @PostMapping("/signup")
    public ProfileDto signup(@RequestBody @Valid SignupDto signupDto, Authentication authentication) throws Exception {
        // maps signupDto to account entity
        Account account = AccountMapper.signupDtoToAccountEntity(signupDto);

        // encrypt the provided password using Bcrypt before saving it to the database.
        String encryptedPassword = encryptionService.encryptString(signupDto.password());

        // put the encrypted password in the account entity before saving it to the database.
        account.setPassword(encryptedPassword);

        // db account is the saved account in the database, and it contains the database id
        Account dbAccount = accountService.save(account)
                    .orElseThrow(() -> new AccountCreationFailureException("failed to save the account to the database "));

        // maps the account entity to AccountDto
        return AccountMapper.AccountEntityToAccountDto(dbAccount);
    }

}
