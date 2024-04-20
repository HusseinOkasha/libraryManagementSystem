package com.example.libraryManagementSystem.security;

import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;
@Service
public class JpaAccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Account account = accountService.findByEmail(username).orElseThrow(s);
        return new AccountDetails(account);
    }
}
