package com.mint.bytelink.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expiration}")
    private Long EXPIRATION;

    public String generateToken(UserDetails userDetails) {

        log.debug("Generating JWT for username: {}",
                userDetails.getUsername());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("JWT generated successfully for username: {}",
                userDetails.getUsername());

        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        boolean valid = !isTokenExpired(token) &&
                extractUsername(token).equals(userDetails.getUsername());

        if (!valid) {
            log.warn("JWT validation failed for username: {}",
                    userDetails.getUsername());
        } else {
            log.debug("JWT validated successfully for username: {}",
                    userDetails.getUsername());
        }

        return valid;
    }

    public String extractUsername(String token) {

        try {
            String username = extractAllClaims(token).getSubject();
            log.debug("Extracted username from JWT: {}", username);
            return username;
        } catch (Exception ex) {
            log.warn("Failed to extract username from JWT: {}",
                    ex.getMessage());
            throw ex;
        }
    }

    public boolean isTokenExpired(String token) {

        try {
            boolean expired = extractAllClaims(token)
                    .getExpiration()
                    .before(new Date(System.currentTimeMillis()));

            if (expired) {
                log.warn("JWT token is expired");
            }

            return expired;

        } catch (Exception ex) {
            log.warn("Failed to check JWT expiration: {}",
                    ex.getMessage());
            throw ex;
        }
    }

    public Claims extractAllClaims(String token) {

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.warn("JWT parsing failed: {}", ex.getMessage());
            throw ex;
        }
    }

    private Key getSigningKey() {

        log.debug("Generating signing key for JWT validation");

        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
