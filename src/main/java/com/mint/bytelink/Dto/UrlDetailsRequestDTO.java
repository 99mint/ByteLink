package com.mint.bytelink.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UrlDetailsRequestDTO {
    @NotNull(message = "Enter a valid Url")
    private String longUrl;
}
