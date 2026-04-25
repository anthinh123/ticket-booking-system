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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/register")
    User register(@RequestBody @Valid UserCreationRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }
}
