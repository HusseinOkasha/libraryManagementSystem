package com.example.libraryManagementSystem.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountAuthenticationProviderService implements AuthenticationProvider {

    @Lazy
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JpaAdminDetailsService accountDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String emailOrPhoneNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails accountDetails =  accountDetailsService.loadUserByUsername(emailOrPhoneNumber);

        return checkPassword(accountDetails, password, bCryptPasswordEncoder);
    }

    private Authentication checkPassword(UserDetails accountDetails, String rawPassword, PasswordEncoder encoder) {

        if(encoder.matches(rawPassword, accountDetails.getPassword())){
            return new UsernamePasswordAuthenticationToken(accountDetails.getUsername(), accountDetails.getPassword(),
                    accountDetails.getAuthorities());
        }
        else{
            throw new BadCredentialsException("Bad Credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
