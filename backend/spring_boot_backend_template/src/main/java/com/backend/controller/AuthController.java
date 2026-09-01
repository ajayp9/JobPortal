package com.backend.controller;
import org.springframework.security.authentication.BadCredentialsException;

import com.backend.exception.BadRequestException;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.backend.dto.request.LoginRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AuthResponse;
import com.backend.dto.response.UserResponse;
import com.backend.entities.Role;
import com.backend.security.JwtUtil;
import com.backend.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserService userService) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> register(

            @RequestParam
            @NotBlank(message = "Name is required")
            @Size(
                    min = 3,
                    max = 30,
                    message = "Name must be between 3 and 30 characters"
            )
            String name,

            @RequestParam
            @NotBlank(message = "Email is required")
            @Email(message = "Please provide a valid email")
            String email,

            @RequestParam
            @NotBlank(message = "Phone number is required")
            @Pattern(
                    regexp = "^[6-9]\\d{9}$",
                    message = "Phone number must be 10 digits and start with 6, 7, 8, or 9"
            )
            String phone,

            @RequestParam
            @NotBlank(message = "Address is required")
            String address,

            @RequestParam
            @NotBlank(message = "Password is required")
            @Size(
                    min = 8,
                    max = 32,
                    message = "Password must be between 8 and 32 characters"
            )
            String password,

            @RequestParam(required = false)
            String coverLetter,

            @RequestParam
            @NotNull(message = "Role is required")
            Role role,
            @RequestParam(required = false) String firstNiche,
            @RequestParam (required = false) String secondNiche,
            @RequestParam (required = false) String thirdNiche,

            @RequestParam(
                    value = "resume",
                    required = false
            )
            MultipartFile resume) {

        RegisterRequest request = new RegisterRequest();

        request.setName(name);
        request.setEmail(email);
        request.setPhone(phone);
        request.setAddress(address);
        request.setPassword(password);
        request.setCoverLetter(coverLetter);
        request.setRole(role);
        request.setFirstNiche(firstNiche);
        request.setSecondNiche(secondNiche);
        request.setThirdNiche(thirdNiche);

        UserResponse response =
                userService.register(request, resume);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping(
            value = "/register-json",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserResponse> registerWithoutResume(
            @Valid @RequestBody RegisterRequest request) {

        UserResponse response =
                userService.register(request, null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String email =
                request.getEmail()
                        .trim()
                        .toLowerCase();

        if (!userService.existsByEmail(email)) {
            throw new BadRequestException(
                    "Wrong email"
            );
        }

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    request.getPassword()
                            )
                    );

            String authenticatedEmail =
                    authentication.getName();

            String token =
                    jwtUtil.generateToken(
                            authenticatedEmail
                    );

            UserResponse userResponse =
                    userService.getCurrentUser(
                            authenticatedEmail
                    );

            AuthResponse response =
                    new AuthResponse(
                            token,
                            "Bearer",
                            userResponse
                    );

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException exception) {

            throw new BadRequestException(
                    "Wrong password"
            );
        }
    }
}