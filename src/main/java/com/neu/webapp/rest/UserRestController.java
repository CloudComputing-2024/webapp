package com.neu.webapp.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.neu.webapp.entity.Role;
import com.neu.webapp.entity.UserEntity;
import com.neu.webapp.repository.RoleRepository;
import com.neu.webapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
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

    @Value("${GOOGLE_CLOUD_PROJECT}")
    private String projectId;

    @Value("${PUBSUB_TOPIC}")
    private String topicId;

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
    public ResponseEntity<String> login(Authentication authentication) throws JsonProcessingException {

        Logger logger = Logger.getLogger(this.getClass().getName());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                                        .orElse(null);

        // Check if user exists and if user is verified
        if (user != null && (user.getVerificationStatus() == null || !user.getVerificationStatus().equals("verified"))) {
            logger.warning("Http 403 Forbidden - User is not verified");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Check if user exists
        if (user == null) {
            logger.severe("Username is not found");
            throw new UsernameNotFoundException("Username is not found");
        }

        logger.info("Http 200 OK - User logged in successfully");
        logger.info("User logged in: " + user.toString());

        String json = objectMapper.writeValueAsString(user);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    @PutMapping("/v1/user/self")
    public ResponseEntity<String> update(Authentication authentication, @RequestBody JsonNode requestBody) {

        Logger logger = Logger.getLogger(this.getClass().getName());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // set auth
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity user = userRepository.findByUsername(authentication.getName())
                                        .orElse(null);

        // Check if user exists and if user is verified
        if (user != null && (user.getVerificationStatus() == null || !user.getVerificationStatus().equals("verified"))) {
            logger.warning("Http 403 Forbidden - User is not verified");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Check if user exists
        if (user == null) {
            logger.severe("Username is not found");
            throw new UsernameNotFoundException("Username is not found");
        }

        // declare a UserEntity object
        UserEntity updatedUser;

        try {
            // convert JasonNode to UserEntity and check for extra invalid fields
            updatedUser = objectMapper.treeToValue(requestBody, UserEntity.class);
        } catch (UnrecognizedPropertyException exception) {
            logger.warning("Http 400 Bad request - Unrecognized property in request body");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            logger.severe("Error converting request body to UserEntity");
            throw new IllegalArgumentException();
        }

        // cannot update username
        if (!updatedUser.getUsername().equals(authentication.getName())) {
            logger.warning("Http 400  Bad request -Cannot update username");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        if (updatedUser.getFirstName() != null) user.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) user.setLastName(updatedUser.getLastName());
        if (updatedUser.getPassword() != null) user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        user.setAccountUpdated();

        // check fields that are not allowed to be updated like account_created and account_updated
        if (updatedUser.getAccountUpdated() != null || updatedUser.getAccountCreated() != null) {
            logger.warning("Http 400  Bad request -Cannot update account_created and account_updated field");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Http 204 No Content - User Updated Successfully");
        logger.info("User updated in: " + user.toString());

        // save user in userRepository
        userRepository.save(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/v1/user")
    public ResponseEntity<String> register(@RequestBody JsonNode requestBody) throws IOException {

        Logger logger = Logger.getLogger(this.getClass().getName());

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        // declare a UserEntity object
        UserEntity theUser;

        try {
            // convert JasonNode to UserEntity and check for extra invalid fields
            theUser = objectMapper.treeToValue(requestBody, UserEntity.class);
        } catch (UnrecognizedPropertyException exception) {
            logger.warning("Http 400 Bad request - Unrecognized property in request body");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            logger.severe("Error converting request body to UserEntity");
            throw new IllegalArgumentException();
        }

        // verify if the username is a email format
        if (!emailPattern.matcher(theUser.getUsername()).find()) {
            logger.warning("Http 400  Bad request - Invalid username format");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check if username already exists in database, not allow duplicate accounts, return bad request
        if (userRepository.existsByUsername(theUser.getUsername())) {
            logger.warning("Http 400 Bad request - Username already exists");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // check fields that are not allowed to be updated like account_created and account_updated
        if (theUser.getAccountUpdated() != null || theUser.getAccountCreated() != null) {
            logger.warning("Http 400 Bad request - Fields that are not allowed to be updated");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // create a new UserEntity for the newUser
        UserEntity newUser = new UserEntity(theUser.getFirstName(), theUser.getLastName(), passwordEncoder.encode(theUser.getPassword()), theUser.getUsername());
        Role userRole = new Role("USER");
        newUser.setRoles(new ArrayList<>(Collections.singletonList(userRole)));

        if(theUser.getVerificationStatus() != null && theUser.getVerificationStatus().equals("verified")){
            newUser.setVerificationStatus("verified");
        }

        userRepository.save(newUser);

        // create a JSON payload
        Map<String, String> payload = new HashMap<>();
        payload.put("username", newUser.getUsername());
        payload.put("firstName", newUser.getFirstName());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // Create a Pub/Sub message with the JSON payload as the data
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                                                   .setData(ByteString.copyFromUtf8(jsonPayload))
                                                   .build();

        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        Publisher publisher = Publisher.newBuilder(topicName).build();
        publisher.publish(pubsubMessage);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        logger.info("Http 201 - User Created Successfully");
        logger.info("User created in: " + newUser.toString());

        // return HttpStatus.CREATED status and user information
        String json = objectMapper.writeValueAsString(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(json);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        Logger logger = Logger.getLogger(this.getClass().getName());

        UserEntity user = userRepository.findByVerificationToken(token);
        if (user != null && user.getVerificationTokenExpiration().after(new Date())) {
            logger.info("User's email is verified successfully");
            user.setVerificationStatus("verified");
            userRepository.save(user);
            return ResponseEntity.ok("Email verified successfully");
        } else {
            logger.warning("Invalid or expired verification.");
            return ResponseEntity.badRequest().body("Invalid or expired verification.");
        }
    }
}