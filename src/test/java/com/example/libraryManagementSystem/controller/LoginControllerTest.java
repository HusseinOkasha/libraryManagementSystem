package com.example.libraryManagementSystem.controller;


import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.service.AdminService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
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

class LoginControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    private static List<Admin> admins = new ArrayList<>();

    @LocalServerPort
    private int port;


    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AdminService adminService;

    @BeforeEach
    void setUp() {
        // "123" encoded with bCrypt
        String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

        // Create 2 accounts
        Account acc1 = new Account("f1", "e1@email.com", "1", AccountType.ADMIN,
                null, null);

        admins.add(new Admin(acc1, bCryptPassword));

        adminService.save(new Admin(acc1, bCryptPassword));
    }

    @AfterEach
    void tearDown() {
        adminService.deleteAll();
    }
    @Test
    public void shouldLogin(){
        String baseUrl = getBaseUrl();
        String email = admins.get(0).getAccount().getEmail();
        String password = "123";

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = email + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);


        HttpEntity<String> request = new HttpEntity<>(basicAuthHeaders);

        String token  = restTemplate.exchange(baseUrl + "/login/admin",
                HttpMethod.POST, request, String.class).getBody();

        assertThat(token).isNotNull();

    }

    @Test
    public void shouldNotLoginWithWrongPassword(){
        String baseUrl = getBaseUrl();
        String email = admins.get(0).getAccount().getEmail();
        String password = "wrongPassword";

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = email + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);


        HttpEntity<String> request = new HttpEntity<>(basicAuthHeaders);
        String token = "";
        try{
             token  = restTemplate.exchange(baseUrl + "/login/admin",
                    HttpMethod.POST, request, String.class).getBody();

        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        assertThat(token).isEmpty();

    }
    @Test
    public void shouldNotLoginWithWrongEmail(){
        String baseUrl = getBaseUrl();
        String email = "wrongemail@email.com";
        String password = "wrongPassword";

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = email + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);


        HttpEntity<String> request = new HttpEntity<>(basicAuthHeaders);

        String token = "";
        try{
            token  = restTemplate.exchange(baseUrl + "/login/admin",
                    HttpMethod.POST, request, String.class).getBody();

        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }

        assertThat(token).isEmpty();
    }


    // utility method to get the base url
    String getBaseUrl(){
        String baseUrl = "http://localhost:" + port + "/api";
        return baseUrl;

    }


}