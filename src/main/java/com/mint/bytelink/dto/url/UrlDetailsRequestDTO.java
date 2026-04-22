package com.mint.bytelink.dto.url;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDetailsRequestDTO {
    @URL
    @NotNull(message = "Enter a valid Url")
    @Schema(example = "https://www.example.com")
    private String longUrl;
}
