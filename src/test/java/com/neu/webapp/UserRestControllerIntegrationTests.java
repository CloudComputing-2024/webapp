package com.neu.webapp;

import com.neu.webapp.entity.UserEntity;
import com.neu.webapp.repository.UserRepository;
import io.restassured.RestAssured;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRestControllerIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @AfterAll
     void cleanupTestData() {
        Optional<UserEntity> user = userRepository.findByUsername("test@gmail.com");

        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new EntityNotFoundException();
        }
    }

    // Test 1 - Use POST call to create an account, and using the GET call, validate account exists.
    @Test
    @Order(1)
    void testCreateUserAndValidateUser() {

        String postUrl = "http://localhost:" + port + "/v1/user";

        // new user's information
        String newUserInfo = "{\"username\":\"test@gmail.com\",\"password\":\"test_password\",\"first_name\":\"test_firstname\",\"last_name\":\"test_lastname\"}";

        // use POST call to create an account
        RestAssured
                .given()
                .contentType("application/json")
                .body(newUserInfo)
                .when()
                .post(postUrl)
                .then()
                .statusCode(201).extract();

        String getUrl = "http://localhost:" + port + "/v1/user/self";

        // use the GET call to validate account exists
        RestAssured
                .given()
                .auth().preemptive().basic("test@gmail.com", "test_password")
                .when()
                .get(getUrl)
                .then()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("first_name", Matchers.equalTo("test_firstname"))
                .body("last_name", Matchers.equalTo("test_lastname"))
                .body("account_created", Matchers.notNullValue())
                .body("account_updated", Matchers.notNullValue())
                .body("username", Matchers.equalTo("test@gmail.com"));
    }

    // Test 2 - Update the account and using the GET call, validate the account was updated.
    @Test
    @Order(2)
    void testUpdateAndValidateUser() {

        String putUrl = "http://localhost:" + port + "/v1/user/self";

        // updated user's information
        String updatedUserInfo = "{\"username\":\"test@gmail.com\",\"password\":\"updatePassword\",\"first_name\":\"update_firstname\",\"last_name\":\"update_lastname\"}";

        // use PUT call to update the user
        RestAssured
                .given()
                .auth().preemptive().basic("test@gmail.com", "test_password")
                .contentType("application/json")
                .body(updatedUserInfo)
                .when()
                .put(putUrl)
                .then()
                .statusCode(204);

        String getUrl = putUrl;

        // use the GET call to login with new password and validate the account was updated.
        RestAssured
                .given()
                .auth().preemptive().basic("test@gmail.com", "updatePassword")
                .when()
                .get(getUrl)
                .then()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("first_name", Matchers.equalTo("update_firstname"))
                .body("last_name", Matchers.equalTo("update_lastname"))
                .body("account_created", Matchers.notNullValue())
                .body("account_updated", Matchers.notNullValue())
                .body("username", Matchers.equalTo("test@gmail.com"));
    }
}
