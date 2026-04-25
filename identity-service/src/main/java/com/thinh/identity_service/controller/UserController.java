package com.thinh.identity_service.controller;

import com.thinh.identity_service.entity.User;
import com.thinh.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserRepository userRepository;

    @GetMapping("/my-info")
    User getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        return userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("USER_NOT_EXISTED"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
