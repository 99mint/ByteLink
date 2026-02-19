package com.mint.bytelink.controller;

import com.mint.bytelink.service.UrlDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/r")
public class RedirectController {

    private final UrlDetailsService urlDetailsService;

    public RedirectController(UrlDetailsService urlDetailsService) {
        this.urlDetailsService = urlDetailsService;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {

        String longUrl = urlDetailsService.getLongUrlByShortCode(shortUrl);

        return ResponseEntity
                .status(307)
                .header("Location", longUrl)
                .build();
    }
}

