package com.neu.webapp;

import com.neu.webapp.entity.UserEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = WebappApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserRestControllerIntegrationTests {

    private static RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl = "http://localhost";

    @Autowired
    private TestH2Repository h2Repository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @Test
    public void testCreateUserAndValidateUser() throws Exception {

        // post call url
        String createUserUrl = baseUrl + ":" + port + "/v1/user";

        // step1:  create a newUser using POST call
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{\"username\":\"jiapan.wei@gmail.com\",\"password\":\"123\",\"first_name\":\"Jiapan\",\"last_name\":\"Wei\"}", headers);
        ResponseEntity<String> createUserResponse = restTemplate.exchange(createUserUrl, HttpMethod.POST, entity, String.class);

        // assert the createUserResponse Http status code is 201, if it is 201, newUser is successfully created
        assertEquals(HttpStatus.CREATED, createUserResponse.getStatusCode());
        assertTrue(Objects.requireNonNull(createUserResponse.getBody()).contains("username='jiapan.wei@gmail.com'"));
        assertEquals(1, h2Repository.findAll().size());

        // step2: validate newUser existence using GET call

        // get call url
        String validateUserUrl = baseUrl + ":" + port + "/v1/user/self";

        // use newUser username and password to set basic auth
        HttpHeaders validateUserHeaders = new HttpHeaders();
        validateUserHeaders.setBasicAuth("jiapan.wei@gmail.com", "123");
        HttpEntity<String> validateUserEntity = new HttpEntity<>(validateUserHeaders);

        // use GET call to log in newUser
        ResponseEntity<String> validateUserResponse = restTemplate.exchange(validateUserUrl, HttpMethod.GET, validateUserEntity, String.class);

        // assert the HTTP status code for validate newUser existence is 200, if it is 200 meaning newUser exists in database
        assertEquals(HttpStatus.OK, validateUserResponse.getStatusCode());

        // check if the response contains the user's correct information to validate existence
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("username='jiapan.wei@gmail.com'"));
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("first_name='Jiapan'"));
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("last_name='Wei'"));
    }

    @Test
    public void testUpdateAndValidateUser() throws Exception {

        // Step 1: Encrypt the password and create the user
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode("123");
        UserEntity user = new UserEntity("Amy", "Wei", encryptedPassword, "amy.wei@gmail.com");
        // save user to repository
        h2Repository.save(user);

        // step 2: use update call to update user
        // PUT call url
        String updateUserUrl = baseUrl + ":" + port + "/v1/user/self";

        // use user's username and password to set basic auth
        HttpHeaders updateUserHeaders = new HttpHeaders();
        updateUserHeaders.setContentType(MediaType.APPLICATION_JSON);
        updateUserHeaders.setBasicAuth("amy.wei@gmail.com", "123");

        // set user's new information
        HttpEntity<String> updateUserEntity = new HttpEntity<>("{\"username\":\"amy.wei@gmail.com\",\"password\":\"updatePassword\",\"first_name\":\"updateFirstName\",\"last_name\":\"updateLastName\"}", updateUserHeaders);

        // use PUT call to update user's information
        ResponseEntity<String> updateUserResponse = restTemplate.exchange(updateUserUrl, HttpMethod.PUT, updateUserEntity, String.class);

        // assert the HTTP status code for update user information successfully is 204 NO_CONTENT
        assertEquals(HttpStatus.NO_CONTENT, updateUserResponse.getStatusCode());

        // step 3: use GET method to validate user info is updated
        // get call url
        String validateUserUrl = baseUrl + ":" + port + "/v1/user/self";

        // use user's username and updated password to set basic auth
        HttpHeaders validateUserHeaders = new HttpHeaders();
        validateUserHeaders.setBasicAuth(user.getUsername(), "updatePassword");
        HttpEntity<String> validateUserEntity = new HttpEntity<>(validateUserHeaders);

        // use GET call to log in updated user
        ResponseEntity<String> validateUserResponse = restTemplate.exchange(validateUserUrl, HttpMethod.GET, validateUserEntity, String.class);

        // assert the HTTP status code for successfully login is 200, if it is 200 meaning updated user can use new password to login
        assertEquals(HttpStatus.OK, validateUserResponse.getStatusCode());

        // check if the response contains correct updated user's information
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("username='amy.wei@gmail.com'"));
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("first_name='updateFirstName'"));
        assertTrue(Objects.requireNonNull(validateUserResponse.getBody()).contains("last_name='updateLastName'"));
    }
}
