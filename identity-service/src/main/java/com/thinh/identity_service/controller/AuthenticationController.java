package com.thinh.identity_service.controller;

import com.thinh.identity_service.dto.request.AuthenticationRequest;
import com.thinh.identity_service.dto.request.UserCreationRequest;
import com.thinh.identity_service.dto.response.AuthenticationResponse;
import com.thinh.identity_service.entity.User;
import com.thinh.identity_service.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    User register(@RequestBody @Valid UserCreationRequest request) {
        log.info("Received registration request for email: {}", request.getEmail());
        User user = authenticationService.register(request);
        log.info("Successfully registered user with email: {}", request.getEmail());
        return user;
    }

    @PostMapping("/login")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        log.info("Received login/authentication request for email: {}", request.getEmail());
        AuthenticationResponse response = authenticationService.authenticate(request);
        log.info("Successfully authenticated user with email: {}", request.getEmail());
        return response;
    }
}
