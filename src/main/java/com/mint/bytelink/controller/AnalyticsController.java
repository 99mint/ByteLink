package com.mint.bytelink.controller;

import com.mint.bytelink.dto.analytics.AnalyticsDTO;
import com.mint.bytelink.service.ClickService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("analytics")

@Tag(name = "Analytics Management", description = "API endpoints for fetching analytics for a short url")

public class AnalyticsController {

    private final ClickService clickService;

    public AnalyticsController(ClickService clickService) {
        this.clickService = clickService;
    }

    @GetMapping("/{shortUrl}")
    @Operation(summary = "Generates analytics for input short url")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200" , description = "Successfully fetched analytics"),
            @ApiResponse(responseCode = "404" , description = "Short url not found"),
            @ApiResponse(responseCode = "401" , description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "User not allowed to access this URL analytics")
    })
    public ResponseEntity<AnalyticsDTO> analyticsForShortUrl(
            @Parameter(description = "Short url", example = "000001")
            @PathVariable String shortUrl){

        log.info("Analytics requested for short url : {} " , shortUrl);
        AnalyticsDTO response = clickService.provideAnalytics(shortUrl);

        log.info("Successfully fetched analytics for short url {} " , shortUrl);
        return ResponseEntity.ok(response);
    }
}
