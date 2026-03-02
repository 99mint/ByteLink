package com.mint.bytelink.service;

import com.mint.bytelink.dto.auth.AuthResponse;
import com.mint.bytelink.entity.User;
import com.mint.bytelink.exception.UserAlreadyExistsException;
import com.mint.bytelink.repository.UserRepository;
import com.mint.bytelink.security.CustomUserDetails;
import com.mint.bytelink.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(User user){
        if (userRepository.existsByEmail(user.getEmail()) ||
                userRepository.existsByUsername((user.getUsername()))){
            throw new UserAlreadyExistsException("user already exists");
        }
        userRepository.save(user);
    }

    public AuthResponse login(String username , String password){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username , password));

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        String accessToken = jwtService.generateToken(new CustomUserDetails(user));
        // Create RefreshTokenService and RefreshTokenRepository
        // also add RefreshToken System here.

        return new AuthResponse(accessToken , " ");
    }
}
