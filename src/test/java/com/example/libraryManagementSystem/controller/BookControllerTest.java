package com.example.libraryManagementSystem.controller;

import com.example.libraryManagementSystem.dto.BookDetailsDto;
import com.example.libraryManagementSystem.dto.BookDto;
import com.example.libraryManagementSystem.entity.Account;
import com.example.libraryManagementSystem.entity.Admin;
import com.example.libraryManagementSystem.entity.Book;
import com.example.libraryManagementSystem.enums.AccountType;
import com.example.libraryManagementSystem.service.AdminService;
import com.example.libraryManagementSystem.service.BookService;
import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = { "spring.datasource.url=jdbc:tc:postgres:latest:///database", "spring.sql.init.mode=always" })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

class BookControllerTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("database").withUsername("myuser");

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private BookService bookService;

    @Autowired
    private AdminService adminService;

    @LocalServerPort
    private int port;

    // holds the authorization token.
    private static String token;

    // represents sample data.
    private static Account acc1, acc2;
    private static Admin admin1, admin2;
    private static Book book1, book2;

    private static String rawPassword = "123";

    // "123" encrypt by bCrypt.
    private static String bCryptPassword = "$2a$12$fdQCjXHktjZczz5hlHg77u8bIXUQdzGQf5k7ulN.cxzhW2vidHzSu";

    @BeforeAll
    static void generalSetup(){

        // create sample admins.
        acc1 =  new Account("name1", "e1@email.com", "1", AccountType.ADMIN, null, null);
        admin1 = new Admin(acc1, bCryptPassword);

        acc2 =  new Account("name2", "e2@email.com", "2", AccountType.ADMIN, null, null);
        admin2 = new Admin(acc2, bCryptPassword);

        // create sample books
        book1 = new Book((long)0, "title1", "author1", "2024","isbn1");
        book2 = new Book((long)0, "title2", "author2", "2024","isbn2");

    }

    @BeforeEach
    void setUp() {
        // save sample admin to the database.
        adminService.save(admin1);

        // perform login and hold the result token.
        token = login(admin1.getAccount().getEmail(), rawPassword, getBaseUrl()+ "/login/admin");

        // save sample books to the database.
        book1 = bookService.save(book1).get();
        book2 = bookService.save(book2).get();
    }

    @AfterEach
    void tearDown() {
        // clear the database.
        adminService.deleteAll();
        bookService.deleteAll();
    }

    @Test
    void ShouldAddNewBook(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        // new book values.
        String title = "title3";
        String author = "author3";
        String publication_year = "2024";
        String isbn = "isbn3";

        BookDetailsDto bookDetailsDto = new BookDetailsDto(title, author, publication_year, isbn);

        HttpEntity<BookDetailsDto> request = new HttpEntity<>(bookDetailsDto, headers);

        ResponseEntity<BookDto> response = restTemplate.exchange(baseUrl + "/books",
                HttpMethod.POST, request, BookDto.class);

        BookDetailsDto createdBookDetails = response.getBody().bookDetailsDto();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createdBookDetails).isNotNull();
        assertThat(createdBookDetails.title()).isEqualTo(title);
        assertThat(createdBookDetails.author()).isEqualTo(author);
        assertThat(createdBookDetails.publication_year()).isEqualTo(publication_year);
        assertThat(createdBookDetails.isbn()).isEqualTo(isbn);
    }

    @Test
    void ShouldGetAllBooks(){
        String baseUrl = getBaseUrl();

        // Create Authorization header which contains the Bearer token
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);


        ParameterizedTypeReference<List<BookDto>>
                responseType = new ParameterizedTypeReference<List<BookDto>>(){};

        ResponseEntity<List<BookDto>> response = restTemplate.exchange(baseUrl + "/books",
                HttpMethod.GET, request, responseType);

        List<BookDto> books = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(books).isNotNull();
        assertThat(books.size()).isEqualTo(2);
    }

    @Test
    void ShouldGetBookById(){
        String baseUrl = getBaseUrl();

        // Create the header object
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(headers);


        ResponseEntity<BookDto> response = restTemplate.exchange(baseUrl + "/books/"+ book1.getId(),
                HttpMethod.GET, request, BookDto.class);

        BookDto book = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(book).isNotNull();
        assertThat(book.bookDetailsDto().isbn()).isEqualTo(book1.getIsbn());
    }

    @Test
    void ShouldUpdateBookById(){
        String baseUrl = getBaseUrl();

        // Create the header object
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);


        BookDetailsDto bookDetailsDto = new BookDetailsDto("updateTitle", "updateAuthor", "2025", "new isbn");
        HttpEntity<BookDetailsDto> request = new HttpEntity<>( bookDetailsDto, headers);


        ResponseEntity<BookDto> response = restTemplate.exchange(baseUrl + "/books/"+ book1.getId(),
                HttpMethod.PUT, request, BookDto.class);

        BookDto book = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(book).isNotNull();
        assertThat(book.bookDetailsDto().title()).isEqualTo(bookDetailsDto.title());
        assertThat(book.bookDetailsDto().author()).isEqualTo(bookDetailsDto.author());
        assertThat(book.bookDetailsDto().publication_year()).isEqualTo(bookDetailsDto.publication_year());
        assertThat(book.bookDetailsDto().isbn()).isEqualTo(bookDetailsDto.isbn());
    }

    @Test
    void ShouldDeleteBookById(){
        String baseUrl = getBaseUrl();

        // Create the header object
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity response= restTemplate.exchange(baseUrl + "/books/"+ book1.getId(),
                HttpMethod.DELETE, request, ResponseEntity.class);

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