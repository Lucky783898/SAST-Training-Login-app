package com.sample.login_application.controllers;

import com.sample.login_application.dtos.ApiResponse;
import com.sample.login_application.dtos.ChangePasswordDto;
import com.sample.login_application.dtos.LoginDto;
import com.sample.login_application.dtos.UserRegistrationDto;
import com.sample.login_application.models.User;
import com.sample.login_application.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody UserRegistrationDto dto) {
        userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginDto dto, HttpSession session) {
        User user = userService.login(dto);
        // Storing user ID in session instead of the whole object is a better practice
        session.setAttribute("userId", user.getId());

        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", user));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<User>> getProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Please log in first", null));
        }

        // Assuming you added a getUserById method in your UserService
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile retrieved", user));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordDto dto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Please log in first", null));
        }

        userService.changePassword(userId, dto);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password updated successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully", null));
    }
}