package com.mint.bytelink.service;

import com.mint.bytelink.entity.RefreshToken;
import com.mint.bytelink.entity.User;
import com.mint.bytelink.exception.other.ResourceNotFoundException;
import com.mint.bytelink.exception.security.InvalidTokenException;
import com.mint.bytelink.repository.RefreshTokenRepository;
import com.mint.bytelink.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {

        log.debug("Creating refresh token for username: {}", username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found while creating refresh token: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        refreshTokenRepository.findByUser(user).ifPresent(existing -> {
            log.debug("Existing refresh token found for user {}, deleting old token", username);
            refreshTokenRepository.delete(existing);
        });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setRevoked(false);

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        log.info("Refresh token created successfully for username: {}", username);

        return savedToken;
    }

    public Optional<RefreshToken> findByToken(String token) {

        log.debug("Looking up refresh token");

        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Expired refresh token detected for username: {}",
                    token.getUser().getUsername());
            refreshTokenRepository.delete(token);
            throw new InvalidTokenException("Refresh token has expired. Please login again.");
        }

        if (token.isRevoked()) {
            log.warn("Revoked refresh token used by username: {}",
                    token.getUser().getUsername());
            throw new InvalidTokenException("Refresh token has been revoked. Please login again.");
        }

        return token;
    }

    @Transactional
    public void revokeToken(String token) {

        log.info("Revoking refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Attempt to revoke non-existing refresh token");
                    return new ResourceNotFoundException("Refresh token not found");
                });

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token revoked for username: {}",
                refreshToken.getUser().getUsername());
    }

    @Transactional
    public void revokeAllUserTokens(String username) {

        log.info("Revoking all refresh tokens for username: {}", username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found while revoking all tokens: {}", username);
                    return new ResourceNotFoundException("User not found: " + username);
                });

        refreshTokenRepository.deleteByUser(user);

        log.info("All refresh tokens revoked for username: {}", username);
    }

    @Transactional
    public void deleteRefreshToken(String token) {

        log.debug("Deleting refresh token");

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Attempt to delete non-existing refresh token");
                    return new ResourceNotFoundException("Refresh token not found");
                });

        refreshTokenRepository.delete(refreshToken);

        log.info("Refresh token deleted for username: {}",
                refreshToken.getUser().getUsername());
    }

    @Transactional
    public void deleteExpiredTokens() {

        log.info("Starting scheduled cleanup of expired refresh tokens");

        refreshTokenRepository.deleteExpiredTokens();

        log.info("Expired refresh token cleanup completed");
    }

    public boolean isTokenValid(String token) {

        Optional<RefreshToken> refreshToken =
                refreshTokenRepository.findByToken(token);

        if (refreshToken.isEmpty()) {
            log.debug("Refresh token validation failed - token not found");
            return false;
        }

        RefreshToken rt = refreshToken.get();

        boolean valid = !rt.isRevoked() &&
                rt.getExpiryDate().isAfter(Instant.now());

        if (!valid) {
            log.debug("Refresh token validation failed for username: {}",
                    rt.getUser().getUsername());
        }

        return valid;
    }

    public User getUserFromToken(String token) {

        log.debug("Extracting user from refresh token");

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> {
                            log.warn("Invalid refresh token used to extract user");
                            return new ResourceNotFoundException("Refresh token not found");
                        });

        verifyExpiration(refreshToken);

        return refreshToken.getUser();
    }
}
