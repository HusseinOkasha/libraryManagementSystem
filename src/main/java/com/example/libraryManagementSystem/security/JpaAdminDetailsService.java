package com.example.libraryManagementSystem.security;

import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;
@Service
public class JpaAdminDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supplier<UsernameNotFoundException> s =
                () -> new UsernameNotFoundException("Problem during authentication!");
        Admin admin = adminService.findByAccount_Email(username).orElseThrow(s);
        return new AdminDetails(admin);
    }
}
