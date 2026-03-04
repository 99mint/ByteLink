package com.mint.bytelink.service;

import com.mint.bytelink.dto.auth.AuthResponse;
import com.mint.bytelink.entity.RefreshToken;
import com.mint.bytelink.entity.User;
import com.mint.bytelink.exception.other.UserAlreadyExistsException;
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
    private final RefreshTokenService refreshTokenService;

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

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(username);

        return new AuthResponse(accessToken , refreshToken.getToken());
    }

    public AuthResponse refreshToken(String refreshTokenString) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenString)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(new CustomUserDetails(user));

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    public void logout(String refreshTokenString) {
        refreshTokenService.revokeToken(refreshTokenString);
    }
}
