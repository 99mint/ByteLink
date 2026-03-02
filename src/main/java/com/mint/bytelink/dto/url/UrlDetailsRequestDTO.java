package com.mint.bytelink.dto.url;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class UrlDetailsRequestDTO {
    @URL
    @NotNull(message = "Enter a valid Url")
    private String longUrl;
}
