package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.SignupDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.service.AdminService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SignupControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    private static String token;
    private static List<Admin> admins = new ArrayList<>();

    @LocalServerPort
    private int port;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AdminService adminService;

    @BeforeAll
    static void generalSetUp(){
        // "123" encoded with bCrypt
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // Create 2 accounts
        Account acc1 = new Account("f1", "e1@email.com", "1", AccountType.ADMIN,
                null, null);

        Account acc2 = new Account("f2", "e2@email.com", "2", AccountType.ADMIN,
                null, null);

        // add newly created accounts to the accounts list.
        Admin admin1 =  new Admin(acc1, bCryptPassword);
        Admin admin2 =  new Admin(acc2, bCryptPassword);

        admins.add(admin1);
        admins.add(admin2);

    }

    @BeforeEach
    void setUp() {
        adminService.save(admins.get(0));
    }

    @AfterEach
    void tearDown() {
        adminService.deleteAll();
    }

    @Test
    void shouldSignup(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new  admin values
        String name = "f2";
        String email = "e2@email.com";
        String phoneNumber = "2";
        String password = "123";

        // initialize the dto
        SignupDto signupDto = new SignupDto(name , email, phoneNumber, password, AccountType.ADMIN);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SignupDto> request = new HttpEntity<>(signupDto, headers);

        ProfileDto profileDto = restTemplate.exchange(baseUrl + "/signup",
                HttpMethod.POST, request, ProfileDto.class).getBody();

        assertThat(profileDto).isNotNull();
        assertThat(profileDto.name()).isEqualTo(name);
        assertThat(profileDto.email()).isEqualTo(email);
        assertThat(profileDto.phoneNumber()).isEqualTo(phoneNumber);
    }

    @Test
    void shouldNotSignup(){
        // As port number as it's generated randomly.
        String baseUrl = getBaseUrl();

        // new  admin values
        String name = "f1";
        String email = "e1@email.com";
        String phoneNumber = "1";
        String password = "123";

        // initialize the dto
        SignupDto signupDto = new SignupDto(name , email, phoneNumber, password, AccountType.ADMIN);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<SignupDto> request = new HttpEntity<>(signupDto, headers);

        try{
            HttpStatusCode responseCode = restTemplate.exchange(baseUrl + "/signup",
                    HttpMethod.POST, request, HttpStatusCode.class).getStatusCode();
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    // utility method to get the base url
    String getBaseUrl(){
        String baseUrl = "http://localhost:" + port + "/api";
        return baseUrl;

    }
}