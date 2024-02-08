package com.neu.webapp.security;

import com.neu.webapp.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfigurer { // define a Spring Security configuration class

    // declare a CustomUserDetailsService for authentication
    private final CustomUserDetailsService userDetailsService;

    // autowire the userDetailsService in the constructor
    @Autowired
    public SecurityConfigurer(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // define HTTP security rules in SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/v1/user/self").authenticated()
                        .requestMatchers("/v1/user").permitAll()
                        .requestMatchers("/healthz").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .headers(headers -> headers
                        .xssProtection(xss -> xss.disable())
                );

        return http.build();
    }

    // define a bean for authenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // define a bean for the password encoder, using Bcrypt hashing password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
