package com.neu.webapp.entity;

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

    @JsonProperty("password")
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
    private ZonedDateTime accountCreated;

    @JsonProperty("account_updated")
    @Column(name = "account_updated")
    private ZonedDateTime accountUpdated;

    // Define a many-to-many relationship between users and roles
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
        accountUpdated = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    public void setAccountUpdated() {
        this.accountUpdated = ZonedDateTime.now(ZoneId.of("UTC"));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_Name='" + firstName + '\'' +
                ", last_Name='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", account_Created=" + accountCreated +
                ", account_Updated=" + accountUpdated +
                '}';
    }
}
