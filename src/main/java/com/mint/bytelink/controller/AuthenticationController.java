package com.mint.bytelink.controller;

import com.mint.bytelink.dto.auth.AuthResponse;
import com.mint.bytelink.dto.auth.LogInRequestViaUsername;
import com.mint.bytelink.dto.auth.RegisterRequest;
import com.mint.bytelink.service.AuthenticationService;
import com.mint.bytelink.util.AuthMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        authenticationService.register(authMapper.toUserEntity(registerRequest));
        return ResponseEntity.ok("User Registered Successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login (@Valid @RequestBody LogInRequestViaUsername logInRequest){
        AuthResponse authResponse = authenticationService.login(logInRequest.getUsername(),
                logInRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }
}
