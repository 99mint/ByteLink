package com.mint.bytelink.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Enter a valid email")
    @Schema(example = "user123email@gmail.com")
    private String email;

    @NotBlank(message = "Please enter a password")
    @Schema(example = "pass123")
    private String password;

    @NotBlank(message = "Name is required")
    @Schema(example = "john")
    private String name;

    @NotBlank(message = "Username Required")
    @Schema(example = "user123")
    private String username;
}
