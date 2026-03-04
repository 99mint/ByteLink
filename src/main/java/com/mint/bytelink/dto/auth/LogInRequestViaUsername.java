package com.mint.bytelink.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogInRequestViaUsername {
    @NotNull
    @Schema(example = "user123")
    private String username;

    @NotNull
    @Schema(example = "pass123")
    private String password;
}
