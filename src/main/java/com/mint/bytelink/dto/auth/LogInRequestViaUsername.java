package com.mint.bytelink.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogInRequestViaUsername {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
