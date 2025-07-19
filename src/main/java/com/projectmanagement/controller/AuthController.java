package com.projectmanagement.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projectmanagement.DTO.EmailRequest;
import com.projectmanagement.config.JwtProvider;
import com.projectmanagement.model.User;
import com.projectmanagement.repository.UserRepository;
import com.projectmanagement.request.LoginRequest;
import com.projectmanagement.response.AuthResponse;
import com.projectmanagement.service.CustomUserServiceImpl;
import com.projectmanagement.service.EmailService;
import com.projectmanagement.service.SubcriptionService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomUserServiceImpl customUserServiceImpl;

    @Autowired
    private SubcriptionService subcriptionService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        User isUserExist = userRepository.findByEmail(user.getEmail());

        if (isUserExist != null) {
            throw new Exception("email already exist with another account");
        }

        User createdUser = new User();
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());

        @SuppressWarnings("unused")
        User savedUser = userRepository.save(createdUser);

        subcriptionService.createSubscription(savedUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generativeToken(authentication);

        AuthResponse res = new AuthResponse();

        res.setMessage("signup Success");
        res.setJwt(jwt);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody EmailRequest request) throws MessagingException {
        String email = request.getEmail();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found with this email.");
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        // ✅ Fix: pass token in the path, not query param
        String link = "https://project-tracker-hp.vercel.app/reset-password/" + token;
        // String link = "http://localhost:5173/reset-password/" + token;
        emailService.sendEmailWithToken(email, link);

        return ResponseEntity.ok("Reset password link sent to email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (token == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Token and new password are required.");
        }

        User user = userRepository.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // clear token after use
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful.");
    }

    @PostMapping("/signing")
    public ResponseEntity<AuthResponse> signing(@RequestBody LoginRequest loginRequest) {
        String userName = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(userName, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generativeToken(authentication);

        AuthResponse res = new AuthResponse();

        res.setMessage("signing Success");
        res.setJwt(jwt);

        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String userName, String password) throws UsernameNotFoundException {
        UserDetails userDetails = customUserServiceImpl.loadUserByUsername(userName);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username");
        }
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
