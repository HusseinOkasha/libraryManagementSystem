package com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers;

import com.example.libraryManagementSystem.dto.PatronDto;
import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Patron;

public class PatronMapper {

    public static PatronDto patronEntityToPatronDto(Patron patron){
        ProfileDto patronProfileDto = AccountMapper.AccountEntityToAccountDto(patron.getAccount());
        return new PatronDto(patron.getId(), patronProfileDto,patron.getBorrowingRecord(), patron.getCreatedAt(),
                patron.getUpdatedAt());
    }
}
