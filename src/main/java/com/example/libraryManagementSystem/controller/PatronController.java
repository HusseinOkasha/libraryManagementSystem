package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.PatronDto;
import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Patron;
import com.example.libraryManagementSystem.exception.AccountCreationFailureException;
import com.example.libraryManagementSystem.exception.FailedToSaveToTheDatabaseException;
import com.example.libraryManagementSystem.exception.PatronNotFoundException;
import com.example.libraryManagementSystem.service.AccountService;
import com.example.libraryManagementSystem.service.PatronService;
import com.example.libraryManagementSystem.util.EntitiesAndDTOsMappers.PatronMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class PatronController {
    @Autowired
    private final PatronService patronService;

    @Autowired
    private final AccountService accountService;

    public PatronController(PatronService patronService, AccountService accountService) {
        this.patronService = patronService;
        this.accountService = accountService;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @PostMapping("/patrons")
    public PatronDto addNewPatron(@RequestBody @Valid SignupDto signupDto) throws Exception {
        // check if there is already account with this email
        Optional<Account> result = accountService.findByEmail(signupDto.email());
        if(result.isPresent()){
            // in case the provided email already exists
            // it will throw this exception with response status code conflict 409
            throw new AccountCreationFailureException("this email already exists.");
        }

        Account account = new Account(signupDto.name(), signupDto.email(), signupDto.phoneNumber(),
                signupDto.accountType(),null, null);

        Patron patron =  new Patron(account);

        return PatronMapper.patronEntityToPatronDto(patronService.save(patron).
                orElseThrow(()->new FailedToSaveToTheDatabaseException("failed due to database error")));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @GetMapping("/patrons")
    public List<PatronDto> getAllPatrons(){
        List<Patron> result = patronService.findAll();
        return result.stream().map(PatronMapper::patronEntityToPatronDto).toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @GetMapping("/patrons/{id}")
    public PatronDto getPatronById(@PathVariable Long id) throws Exception {
        Optional<Patron> result = patronService.findById(id);
        return PatronMapper.patronEntityToPatronDto(
                result.orElseThrow(()-> new PatronNotFoundException("There is no patron with id: " + id)));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @PutMapping("/patrons/{id}")
    public PatronDto updatePatronById(@PathVariable @PositiveOrZero Long id, @RequestBody @Valid ProfileDto profileDto)
            throws Exception {
        // fetch the patron from the database.
        Optional<Patron> result = patronService.findById(id);

        // check the case of absence of a patron with the give id.
        Patron dbPatron = result.orElseThrow(()-> new PatronNotFoundException("there is no patron with this id:" + id));

        // update the dbPatron with the provided values.
        // 1) get the patron account.
        Account dbAccount  = dbPatron.getAccount();

        // 2) update the patron's account with the provided values.
        dbAccount.setName(profileDto.name());
        dbAccount.setPhoneNumber(profileDto.phoneNumber());
        // I didn't support email change as I depend on it as identifier in spring security.
        // solution_1 to this issue is to depend on the database id something that doesn't change.
        // solution_2 is to generate new token when the email changes.

        // reflect the updates to the database.
        result  = patronService.save(dbPatron);
        return PatronMapper.patronEntityToPatronDto(result.orElseThrow(()-> new FailedToSaveToTheDatabaseException(
                "failed to save the updates to the database")));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Validated
    @DeleteMapping("/patrons/{id}")
    public void deletePatronById(@PathVariable @PositiveOrZero Long id){
        patronService.deleteById(id);
    }
}
