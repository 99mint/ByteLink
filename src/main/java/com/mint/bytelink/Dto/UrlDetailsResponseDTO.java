package com.mint.bytelink.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UrlDetailsResponseDTO {

    private String longUrl;

    private String shortUrl;

    private LocalDateTime createdAt;

    private LocalDateTime expiration;
}
