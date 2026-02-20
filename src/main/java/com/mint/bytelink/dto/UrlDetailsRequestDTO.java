package com.mint.bytelink.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlDetailsRequestDTO {
    @NotNull(message = "Enter a valid Url")
    private String longUrl;
}
