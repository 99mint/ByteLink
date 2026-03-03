package com.mint.bytelink.controller;

import com.mint.bytelink.dto.url.UrlDetailsRequestDTO;
import com.mint.bytelink.dto.url.UrlDetailsResponseDTO;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.service.UrlDetailsService;
import com.mint.bytelink.util.UrlDetailsMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/links")
public class UrlDetailsController {

    private final UrlDetailsService urlDetailsService;
    private final UrlDetailsMapper urlDetailsMapper;

    public UrlDetailsController(UrlDetailsService urlDetailsService,
                                UrlDetailsMapper urlDetailsMapper) {
        this.urlDetailsService = urlDetailsService;
        this.urlDetailsMapper = urlDetailsMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<UrlDetailsResponseDTO> shortenUrl(
            @Valid @RequestBody UrlDetailsRequestDTO urlDetailsRequestDTO)
            throws URISyntaxException {

        log.info("Shorten URL request received for longUrl: {}",
                urlDetailsRequestDTO.getLongUrl());

        UrlDetails urlDetails =
                urlDetailsService.shortenUrl(urlDetailsRequestDTO.getLongUrl());

        log.info("Short URL '{}' generated for longUrl: {}",
                urlDetails.getShortUrl(),
                urlDetails.getLongUrl());

        return ResponseEntity.ok(
                urlDetailsMapper.toResponseDto(urlDetails)
        );
    }

    @GetMapping
    public ResponseEntity<List<UrlDetailsResponseDTO>> getAllUrlsByLongUrl(
            @RequestParam String longUrl) {

        log.info("Fetching all short URLs for longUrl: {}", longUrl);

        List<UrlDetails> urlDetails =
                urlDetailsService.getAllUrlByLongUrl(longUrl);

        log.info("Found {} short URLs for longUrl: {}",
                urlDetails.size(),
                longUrl);

        return ResponseEntity.ok(
                urlDetailsMapper.toResponseDtoList(urlDetails)
        );
    }

    @DeleteMapping("/{shortUrl}")
    public void deleteUrlDetails(@PathVariable String shortUrl) {

        log.info("Delete request received for shortUrl: {}", shortUrl);

        urlDetailsService.deleteUrl(shortUrl);

        log.info("ShortUrl '{}' deleted successfully", shortUrl);
    }

    @PutMapping("/{shortUrl}")
    public ResponseEntity<UrlDetailsResponseDTO> changeLongUrl(
            @PathVariable String shortUrl,
            @Valid @RequestBody UrlDetailsRequestDTO urlDetailsRequestDTO) {

        log.info("Update request received for shortUrl: {}",
                shortUrl);

        UrlDetails updated =
                urlDetailsService.changeLongUrl(
                        shortUrl,
                        urlDetailsRequestDTO.getLongUrl()
                );

        log.info("ShortUrl '{}' updated with new longUrl: {}",
                shortUrl,
                updated.getLongUrl());

        return ResponseEntity.ok(
                urlDetailsMapper.toResponseDto(updated)
        );
    }
}
