package com.mint.bytelink.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogInRequestViaUsername {
    @NotBlank
    @Schema(example = "user123")
    private String username;

    @NotBlank
    @Schema(example = "pass123")
    private String password;
}
