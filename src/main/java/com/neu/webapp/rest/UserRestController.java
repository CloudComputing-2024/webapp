package com.neu.webapp.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.neu.webapp.entity.Role;
import com.neu.webapp.entity.UserEntity;
import com.neu.webapp.repository.RoleRepository;
import com.neu.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Pattern;

@RestController
//@RequestMapping("/v1/user")
public class UserRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    // Define the email pattern
    private static final Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    public UserRestController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/v1/user/self")
    public ResponseEntity<String> login(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserEntity user = userRepository.findByUsername(authentication.getName())
                                        .orElseThrow(() -> new UsernameNotFoundException("Username is not found"));
        return ResponseEntity.status(HttpStatus.OK).body(user.toString());
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<String> update(Authentication authentication, @RequestBody JsonNode requestBody) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // declare a UserEntity object
        UserEntity updatedUser;

        try {
            // convert JasonNode to UserEntity and check for extra invalid fields
            updatedUser = objectMapper.treeToValue(requestBody, UserEntity.class);
        } catch (UnrecognizedPropertyException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            throw new IllegalArgumentException();
        }

        // set auth
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Cannot update username
        if (!updatedUser.getUsername().equals(authentication.getName())) {
            System.out.println("updatedUser: " + updatedUser.getUsername() + "authentication: " + authentication.getName());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userRepository.findByUsername(authentication.getName())
                                        .orElseThrow(() -> new UsernameNotFoundException("Username is not found"));

        if (updatedUser.getFirstName() != null) user.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) user.setLastName(updatedUser.getLastName());
        if (updatedUser.getPassword() != null) user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setAccountUpdated();

        // check fields that are not allowed to be updated like account_created and account_updated
        if (updatedUser.getAccountUpdated() != null || updatedUser.getAccountCreated() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // save user in userRepository
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/v1/user")
    public ResponseEntity<String> register(@RequestBody JsonNode requestBody) {

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // declare a UserEntity object
        UserEntity theUser;

        try {
            // convert JasonNode to UserEntity and check for extra invalid fields
            theUser = objectMapper.treeToValue(requestBody, UserEntity.class);
        } catch (UnrecognizedPropertyException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            throw new IllegalArgumentException();
        }

        // verify if the username is a email format
        if (!emailPattern.matcher(theUser.getUsername()).find()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if username already exists in database, not allow duplicate accounts, return bad request
        if (userRepository.existsByUsername(theUser.getUsername())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        // check fields that are not allowed to be updated like account_created and account_updated
        if (theUser.getAccountUpdated() != null || theUser.getAccountCreated() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // create a new UserEntity for the newUser
        UserEntity newUser = new UserEntity(theUser.getFirstName(), theUser.getLastName(), passwordEncoder.encode(theUser.getPassword()), theUser.getUsername());
        Role userRole = new Role("USER");
        newUser.setRoles(new ArrayList<>(Collections.singletonList(userRole)));

        userRepository.save(newUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);


        // return HttpStatus.CREATED status and user information
        return ResponseEntity.status(HttpStatus.CREATED)
                             .headers(headers)
                             .body(newUser.toString());
    }
}
