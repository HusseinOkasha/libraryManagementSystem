package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private final TokenService tokenService;

    public LoginController(TokenService tokenService){
        this.tokenService = tokenService;
    }
    @PostMapping("/admin")
    public String adminLogin(Authentication authentication) {

        return tokenService.generateToken(authentication.getName(), AccountType.ADMIN);
    }
}
