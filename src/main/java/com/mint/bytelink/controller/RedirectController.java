package com.mint.bytelink.controller;

import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.service.ClickService;
import com.mint.bytelink.service.UrlDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/r")
public class RedirectController {

    private final UrlDetailsService urlDetailsService;
    private final ClickService clickService;

    public RedirectController(UrlDetailsService urlDetailsService, ClickService clickService) {
        this.urlDetailsService = urlDetailsService;
        this.clickService = clickService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl , HttpServletRequest request) {

        String longUrl = urlDetailsService.getLongUrlByShortCode(shortUrl);

        UrlDetails urlDetails = urlDetailsService.getUrlByShortCode(shortUrl);

        urlDetailsService.incrementClickCounter(urlDetails.getShortUrl());
        clickService.recordClick(urlDetails, request);

        return ResponseEntity
                .status(307)
                .header("Location", longUrl)
                .build();
    }
}

