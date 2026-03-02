package com.mint.bytelink.util;

import com.mint.bytelink.dto.auth.RegisterRequest;
import com.mint.bytelink.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {
    private final PasswordEncoder passwordEncoder;

    public AuthMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public User toUserEntity(RegisterRequest registerRequest){
        User user = new User();

        user.setName(registerRequest.getName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());

        return user;
    }
}
