package com.mint.bytelink.dto.auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Enter a valid email")
    private String email;

    @NotNull(message = "Please enter a password")
    private String password;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Username Required")
    private String username;
}
