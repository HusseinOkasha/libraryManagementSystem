package com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers;

import com.example.libraryManagementSystem.dto.BorrowingRecordDto;
import com.example.libraryManagementSystem.dto.PatronDto;
import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Patron;

import java.util.stream.Collectors;

public class PatronMapper {

    public static PatronDto patronEntityToPatronDto(Patron patron){
        ProfileDto patronProfileDto = AccountMapper.accountEntityToProfileDto(patron.getAccount());
        return new PatronDto(patron.getId(), patronProfileDto,patron.getBorrowingRecord().stream().map((e)-> new BorrowingRecordDto(e.getPatron().getId(), e.getBook().getId() , e.getStatus())).collect(Collectors.toSet()));
    }
}
