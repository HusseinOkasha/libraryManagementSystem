package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.dto.PatronDto;
import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.dto.UpdatePatronDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.entity.Patron;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.service.AdminService;
import com.example.libraryManagementSystem.service.PatronService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PatronControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private PatronService patronService;

    @Autowired
    private AdminService adminService;

    @LocalServerPort
    private int port;

    // holds the authorization token.
    private static String token;
    private static Account account;
    private static Admin admin;
    private static Patron patron1;
    private static Patron patron2;


    private static String rawPassword = "123";

    // "123" encrypt by bCrypt.
    private static String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

    @BeforeAll
    static void generalSetup(){

        // create sample admin.
        account =  new Account("name1", "e1@email.com", "1", AccountType.ADMIN, null, null);
        admin = new Admin(account, bCryptPassword);

        // create sample Patrons
        patron1 = new Patron(new Account("patron1", "e2@email.com", "2", AccountType.PATRON, null, null));
        patron2 = new Patron(new Account("patron2", "e3@email.com", "3", AccountType.PATRON, null, null));
    }


    @BeforeEach
    void setUp() {
        // save sample admin to the database
        adminService.save(admin);

        //perform login and hold the result token.
        token = login(admin.getAccount().getEmail(), rawPassword, getBaseUrl() + "/login/admin");

        // save sample patrons to the database.
        patron1 = patronService.save(patron1).get();
        patron2 = patronService.save(patron2).get();

    }

    @AfterEach
    void tearDown() {
        adminService.deleteAll();
        patronService.deleteAll();
    }

    @Test
    void shouldAddNewPatron(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        String name = "patron3";
        String email = "e4@email.com";
        String phoneNumber = "4";

        PatronDto patronDto = new PatronDto((long)0, new ProfileDto(name, email, phoneNumber, AccountType.PATRON,null, null), null);
        HttpEntity<PatronDto> request =  new HttpEntity<>(patronDto, headers);

        ResponseEntity<PatronDto> response = restTemplate.exchange(baseUrl + "/patrons",
                HttpMethod.POST, request, PatronDto.class);

        ProfileDto createdPatronProfile = response.getBody().profileDto();

        assertThat(createdPatronProfile).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdPatronProfile.name()).isEqualTo(name);
        assertThat(createdPatronProfile.email()).isEqualTo(email);
        assertThat(createdPatronProfile.phoneNumber()).isEqualTo(phoneNumber);

    }

    @Test
    void shouldGetAllPatrons(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<PatronDto> request =  new HttpEntity<>(headers);

        ParameterizedTypeReference<List<PatronDto>>
                responseType = new ParameterizedTypeReference<List<PatronDto>>(){};

        ResponseEntity<List<PatronDto>> response = restTemplate.exchange(baseUrl + "/patrons",
                HttpMethod.GET, request, responseType);

        List<PatronDto>patrons = response.getBody();

        assertThat(patrons).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(patrons.size()).isEqualTo(2);

    }

    @Test
    void ShouldGetPatronById(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<PatronDto> request =  new HttpEntity<>(headers);

        ResponseEntity<PatronDto> response = restTemplate.exchange(baseUrl + "/patrons/"+patron1.getId(),
                HttpMethod.GET, request, PatronDto.class);

        ProfileDto profileDto = response.getBody().profileDto();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(profileDto.name()).isEqualTo(patron1.getAccount().getName());
        assertThat(profileDto.email()).isEqualTo(patron1.getAccount().getEmail());
        assertThat(profileDto.phoneNumber()).isEqualTo(patron1.getAccount().getPhoneNumber());
        
    }
    @Test
    void shouldUpdatePatronById(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        String name = "nameUpdated";
        String phoneNumber = "4";

        UpdatePatronDto updatePatronDto = new UpdatePatronDto(name,phoneNumber);
        HttpEntity<UpdatePatronDto> request =  new HttpEntity<>(updatePatronDto, headers);

        ResponseEntity<PatronDto> response = restTemplate.exchange(baseUrl + "/patrons/"+patron1.getId(),
                HttpMethod.PUT, request, PatronDto.class);

        ProfileDto updatedPatronProfile = response.getBody().profileDto();

        assertThat(updatedPatronProfile).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedPatronProfile.name()).isEqualTo(name);
        assertThat(updatedPatronProfile.phoneNumber()).isEqualTo(phoneNumber);

    }

    @Test
    void shouldDeletePatronById(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<UpdatePatronDto> request =  new HttpEntity<>(headers);
        ResponseEntity<PatronDto> response = restTemplate.exchange(baseUrl + "/patrons/"+patron1.getId(),
                HttpMethod.DELETE, request, PatronDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    }



    // utility method to encapsulate the login logic.
    String login(String username, String password, String url){

        // Create the basic auth request to the api/login/owner endpoint.
        String plainCredentials  = username + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials= new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);

        // Perform login to get the token.
        HttpEntity<String> basicAuthRequest = new HttpEntity<String>(basicAuthHeaders);

        String  token = restTemplate.postForObject(url, basicAuthRequest
                ,String.class);

        return token;
    }
    
    
    // utility method to get the base url
    String getBaseUrl(){
        String baseUrl = "http://localhost:" + port + "/api";
        return baseUrl;

    }

}