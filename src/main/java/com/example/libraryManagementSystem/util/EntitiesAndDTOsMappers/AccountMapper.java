package com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers;

import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;

// this class encapsulates the mapping logic from account entity to different account related DTOs.
public class AccountMapper {

    // maps account entity to account dto
    public static ProfileDto AccountEntityToAccountDto(Account account){
        return new ProfileDto(account.getName(), account.getEmail(), account.getPhoneNumber(),
                account.getAccountType(), account.getCreatedAt(), account.getUpdatedAt());
    }

    // maps signupDto to Account entity
    public static Account signupDtoToAccountEntity(SignupDto signupDto){
        return new Account(signupDto.name(), signupDto.email(), signupDto.phoneNumber(), signupDto.password(),
                signupDto.accountType(),null, null);
    }
}
