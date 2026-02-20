package com.mint.bytelink.controller;

import com.mint.bytelink.dto.UrlDetailsRequestDTO;
import com.mint.bytelink.dto.UrlDetailsResponseDTO;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.service.UrlDetailsService;
import com.mint.bytelink.util.UrlDetailsMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/links")
public class UrlDetailsController {
    private final UrlDetailsService urlDetailsService;
    private final UrlDetailsMapper urlDetailsMapper;

    public UrlDetailsController(UrlDetailsService urlDetailsService , UrlDetailsMapper urlDetailsMapper){
        this.urlDetailsService = urlDetailsService;
        this.urlDetailsMapper = urlDetailsMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<UrlDetailsResponseDTO> shortenUrl(@Valid @RequestBody UrlDetailsRequestDTO urlDetailsRequestDTO){
        UrlDetails urlDetails = urlDetailsService.shortenUrl(urlDetailsRequestDTO.getLongUrl());
        return ResponseEntity.ok(urlDetailsMapper.toResponseDto(urlDetails));
    }

    @GetMapping
    public ResponseEntity<List<UrlDetailsResponseDTO>> getAllUrlsBuyLongUrl(@RequestParam String longUrl){
        List<UrlDetails> urlDetails = urlDetailsService.getAllUrlByLongUrl(longUrl);
        return ResponseEntity.ok(urlDetailsMapper.toResponseDtoList(urlDetails));
    }

    @DeleteMapping("/{shortUrl}")
    public void deleteUrlDetails(@PathVariable String shortUrl){
        urlDetailsService.deleteUrl(shortUrl);
    }

    @PutMapping("/{shortUrl}")
    public ResponseEntity<UrlDetailsResponseDTO> changeLongUrl(@PathVariable String shortUrl , @Valid @RequestBody UrlDetailsRequestDTO urlDetailsRequestDTO){
        return ResponseEntity.ok(urlDetailsMapper.toResponseDto(urlDetailsService.changeLongUrl(shortUrl , urlDetailsRequestDTO.getLongUrl())));
    }
}
