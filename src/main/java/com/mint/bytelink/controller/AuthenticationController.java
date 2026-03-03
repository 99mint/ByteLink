package com.mint.bytelink.controller;

import com.mint.bytelink.dto.auth.AuthResponse;
import com.mint.bytelink.dto.auth.LogInRequestViaUsername;
<<<<<<< HEAD
=======
import com.mint.bytelink.dto.auth.RefreshTokenRequest;
>>>>>>> d9dd750 (feat: implement JWT authentication and refresh token system)
import com.mint.bytelink.dto.auth.RegisterRequest;
import com.mint.bytelink.service.AuthenticationService;
import com.mint.bytelink.util.AuthMapper;
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
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthMapper authMapper;

    public AuthenticationController(AuthenticationService authenticationService,
                                    AuthMapper authMapper){
        this.authenticationService = authenticationService;
        this.authMapper = authMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register (@Valid @RequestBody RegisterRequest registerRequest){

        log.info("Registration attempt for username: {}", registerRequest.getUsername());

        authenticationService.register(authMapper.toUserEntity(registerRequest));
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LogInRequestViaUsername logInRequest){

        log.info("LogIn attempt for username: {}", logInRequest.getUsername());

        AuthResponse authResponse = authenticationService.login(logInRequest.getUsername(),
                logInRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }
<<<<<<< HEAD
=======

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        log.info("Refresh token request received");

        AuthResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {

        log.info("Logout request received");

        authenticationService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }
>>>>>>> d9dd750 (feat: implement JWT authentication and refresh token system)
}
