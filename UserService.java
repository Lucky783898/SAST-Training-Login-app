package com.sample.login_application.service;

import com.sample.login_application.dtos.ChangePasswordDto;
import com.sample.login_application.dtos.LoginDto;
import com.sample.login_application.dtos.UserRegistrationDto;
import com.sample.login_application.exceptions.InvalidCredentialsException;
import com.sample.login_application.exceptions.UnauthorizedException;
import com.sample.login_application.exceptions.UserAlreadyExistsException;
import com.sample.login_application.models.User;
import com.sample.login_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. User Registration
    public void registerUser(UserRegistrationDto dto) {
        // Check if email already exists in the database
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already in use!");
        }

        // Map DTO to Entity
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword()); // Note: In a real app, use BCryptPasswordEncoder here!

        userRepository.save(user);
    }

    // 2. User Login
    public User login(LoginDto dto) {
        // Find user by email
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("User not found!"));

        // Check password match
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new InvalidCredentialsException("Invalid password!");
        }

        return user;
    }

    // 3. Get User Profile (Used to fetch profile via session ID)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UnauthorizedException("User not found or session expired"));
    }

    // 4. Change Password
    public void changePassword(Long userId, ChangePasswordDto dto) {
        // Fetch the currently logged-in user
        User user = getUserById(userId);

        // Verify the old password matches what is in the database
        if (!user.getPassword().equals(dto.getOldPassword())) {
            throw new InvalidCredentialsException("Old password does not match!");
        }

        // Update to new password and save
        user.setPassword(dto.getNewPassword());
        userRepository.save(user);
    }
}
