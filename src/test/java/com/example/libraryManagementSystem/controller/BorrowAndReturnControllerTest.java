package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.PatronDto;
import com.example.libraryManagementSystem.dto.ProfileDto;
import com.example.libraryManagementSystem.entity.*;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.enums.BookStatus;
import com.example.libraryManagementSystem.service.AdminService;
import com.example.libraryManagementSystem.service.BookService;
import com.example.libraryManagementSystem.service.BorrowingRecordService;
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
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;



@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BorrowAndReturnControllerTest {

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

    @Autowired
    private BookService bookService;

    @Autowired
    private BorrowingRecordService borrowingRecordService;

    @LocalServerPort
    private int port;

    // holds the authorization token.
    private static String token;

    private static Admin admin;
    private static Patron patron1;
    private static Patron patron2;
    private static Book book1;
    private static Book book2;
    private static BorrowingRecord borrowingRecord;

    private static String rawPassword = "123";
    // "123" encrypt by bCrypt.
    private static String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";


    @BeforeAll
    static void generalSetup(){
        // create sample admin.
        admin = new Admin(new Account("name1", "e1@email.com", "1", AccountType.ADMIN, null, null), bCryptPassword);

        // create sample Patrons
        patron1 = new Patron(new Account("patron1", "e2@email.com", "2", AccountType.PATRON, null, null));
        patron2 = new Patron(new Account("patron2", "e3@email.com", "3", AccountType.PATRON, null, null));

        book1 = new Book((long)0 , "title1", "author1", "2024","isbn1");
        book2 = new Book((long)0 , "title2", "author2", "2024","isbn2");
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

        // save sample books to the database;
        book1 = bookService.save(book1).get();
        book2 = bookService.save(book2).get();

        //save sample borrowing record to the database
        borrowingRecord = borrowingRecordService.save(new BorrowingRecord(
                new BorrowingRecord.Id(patron1.getId(), book1.getId()), patron1, book1, BookStatus.BORROWED, null, null)).get();

    }

    @AfterEach
    void tearDown() {
        adminService.deleteAll();
        patronService.deleteAll();
        bookService.deleteAll();
        borrowingRecordService.deleteAll();
    }

    @Test
    void shouldBorrowBook(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request =  new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange(baseUrl + "/borrow/"+book2.getId()+"/patron/"+patron1.getId(),
                HttpMethod.POST, request, void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotBorrowBookAlreadyBorrowed(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request =  new HttpEntity<>(headers);
        try{
            ResponseEntity response = restTemplate.exchange(baseUrl + "/borrow/"+book1.getId()+"/patron/"+patron1.getId(),
                    HttpMethod.POST, request, void.class);
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }

    }

    @Test
    void shouldReturnBook(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request =  new HttpEntity<>(headers);

        ResponseEntity response = restTemplate.exchange(baseUrl + "/borrow/"+book1.getId()+"/patron/"+patron1.getId(),
                HttpMethod.PUT, request, void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldNotReturnBookThatIsNotBorrowed(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity request =  new HttpEntity<>(headers);

        try{
            ResponseEntity response = restTemplate.exchange(baseUrl + "/borrow/"+book2.getId()+"/patron/"+patron1.getId(),
                    HttpMethod.PUT, request, void.class);
        }
        catch (HttpClientErrorException e){
            assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }


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