package com.mint.bytelink.controller;

import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.exception.other.UrlExpiredException;
import com.mint.bytelink.service.ClickService;
import com.mint.bytelink.service.UrlDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/r")
public class RedirectController {

    private final UrlDetailsService urlDetailsService;
    private final ClickService clickService;

    public RedirectController(UrlDetailsService urlDetailsService,
                              ClickService clickService) {
        this.urlDetailsService = urlDetailsService;
        this.clickService = clickService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl , HttpServletRequest request) {

        log.info("Redirect request received for shortUrl: {}", shortUrl);

        UrlDetails urlDetails = urlDetailsService.getUrlByShortCode(shortUrl);

        if (urlDetails.getActiveTill().isBefore(LocalDateTime.now())){

            log.warn("Expired shortUrl access attempt: {}", shortUrl);

            throw new UrlExpiredException("This link has been expired");
        }

        urlDetailsService.incrementClickCounter(urlDetails.getShortUrl());
        clickService.recordClick(urlDetails, request);

        log.info("Redirect successful for shortUrl: {} -> {}",
                shortUrl, urlDetails.getLongUrl());

        return ResponseEntity
                .status(307)
                .header("Location", urlDetails.getLongUrl())
                .build();
    }
}

