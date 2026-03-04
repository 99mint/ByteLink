package com.mint.bytelink.controller;

import com.mint.bytelink.dto.url.UrlDetailsRequestDTO;
import com.mint.bytelink.dto.url.UrlDetailsResponseDTO;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.service.UrlDetailsService;
import com.mint.bytelink.util.UrlDetailsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/links")

@Tag(name = "URL Management", description = "API endpoints for creating and managing short URLs")

public class UrlDetailsController {

    private final UrlDetailsService urlDetailsService;
    private final UrlDetailsMapper urlDetailsMapper;

    public UrlDetailsController(UrlDetailsService urlDetailsService,
                                UrlDetailsMapper urlDetailsMapper) {
        this.urlDetailsService = urlDetailsService;
        this.urlDetailsMapper = urlDetailsMapper;
    }

    @PostMapping("/create")

    @Operation(summary = "Create short URL from long URL")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Short URL generated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL format"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })

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

    @Operation(summary = "Get all short URLs created for a given long URL")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched URLs"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })

    public ResponseEntity<List<UrlDetailsResponseDTO>> getAllUrlsByLongUrl(

            @Parameter(description = "Original long URL", example = "https://google.com")

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

    @Operation(summary = "Delete a short URL")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Short URL deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })

    public void deleteUrlDetails(

            @Parameter(description = "Short URL code", example = "abc123")

            @PathVariable String shortUrl) {

        log.info("Delete request received for shortUrl: {}", shortUrl);

        urlDetailsService.deleteUrl(shortUrl);

        log.info("ShortUrl '{}' deleted successfully", shortUrl);
    }

    @PutMapping("/{shortUrl}")

    @Operation(summary = "Update the long URL for a given short URL")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Short URL updated successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "401", description = "User is not authenticated")
    })

    public ResponseEntity<UrlDetailsResponseDTO> changeLongUrl(

            @Parameter(description = "Short URL code", example = "abc123")

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
