package com.mint.bytelink.controller;

import com.mint.bytelink.dto.auth.AuthResponse;
import com.mint.bytelink.dto.auth.LogInRequestViaUsername;
import com.mint.bytelink.dto.auth.RefreshTokenRequest;
import com.mint.bytelink.dto.auth.RegisterRequest;
import com.mint.bytelink.service.AuthenticationService;
import com.mint.bytelink.util.AuthMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")

@Tag(name = "Authentication Management", description = "API endpoints for authentication and authorization")

public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthMapper authMapper;

    public AuthenticationController(AuthenticationService authenticationService,
                                    AuthMapper authMapper){
        this.authenticationService = authenticationService;
        this.authMapper = authMapper;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "Registration Successful"),
            @ApiResponse(responseCode = "400" , description = "Enter correct data"),
            @ApiResponse(responseCode = "409" , description = "User is already registered")
    })
    public ResponseEntity<String> register (@Valid @RequestBody RegisterRequest registerRequest){

        log.info("Registration attempt for username: {}", registerRequest.getUsername());

        authenticationService.register(authMapper.toUserEntity(registerRequest));
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and generates JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Login Successful"),
            @ApiResponse(responseCode = "401" , description = "Enter correct credentials")
    })
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LogInRequestViaUsername logInRequest){

        log.info("LogIn attempt for username: {}", logInRequest.getUsername());

        AuthResponse authResponse = authenticationService.login(logInRequest.getUsername(),
                logInRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/refresh")
    @Operation(summary = "Takes refresh token and generates new JWT when older one expires")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Generated new jwt token"),
            @ApiResponse(responseCode = "404" , description = "Refresh token does not exist")
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        log.info("Refresh token request received");

        AuthResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoking the refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "404", description = "Refresh token not found")
    })
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {

        log.info("Logout request received");

        authenticationService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
}
