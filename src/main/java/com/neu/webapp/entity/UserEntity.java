package com.neu.webapp.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Accessors(chain = true)
@ToString
@Entity
@Table(name = "users") // Specify the name of the table as "users" in the database
public class UserEntity {

    // id as primary key
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)", unique = true, updatable = false, nullable = false)
    private UUID id;

    @JsonProperty("username")
    @Column(name = "username")
    private String username;

    @JsonProperty(value = "password",access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("account_created")
    @Column(name = "account_created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private ZonedDateTime accountCreated;

    @JsonProperty("account_updated")
    @Column(name = "account_updated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private ZonedDateTime accountUpdated;

    @JsonProperty("verification_token")
    @Column(name = "verification_token")
    private String verificationToken;

    @JsonProperty("verification_token_expiration")
    @Column (name = "verification_token_expiration")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS",timezone = "America/Los_Angeles")
    private Date verificationTokenExpiration;

    @JsonProperty("verification_status")
    @Column(name = "verification_status")
    private String verificationStatus;

    // Define a many-to-many relationship between users and roles
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    public UserEntity() {

    }

    public UserEntity(String theUsername, String thePassword) {
        username = theUsername;
        password = thePassword;
        accountCreated = ZonedDateTime.now(ZoneId.of("UTC"));
        accountUpdated = ZonedDateTime.now(ZoneId.of("UTC"));

    }

    // A constructor with arguments for creating a user with username, password, firstname, lastname
    public UserEntity(String firstName, String lastName, String password, String username) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        accountCreated = ZonedDateTime.now(ZoneId.of("UTC"));
        accountUpdated = accountCreated;
    }

    public void setAccountUpdated() {
        this.accountUpdated = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + firstName + '\'' +
                ", last_name='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", account_created=" + accountCreated.toString() +
                ", account_updated=" + accountUpdated.toString() +
                ", verification_token=" + verificationToken +
                ", verification_status=" + verificationStatus +
                ", verificationToken_expiration=" + verificationTokenExpiration +
                '}';
    }
}
