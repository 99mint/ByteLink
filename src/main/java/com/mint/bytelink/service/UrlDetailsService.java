package com.mint.bytelink.service;

import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.entity.User;
import com.mint.bytelink.exception.ResourceNotFoundException;
import com.mint.bytelink.repository.UrlDetailsRepository;
import com.mint.bytelink.repository.UserRepository;
import com.mint.bytelink.security.CustomUserDetails;
import com.mint.bytelink.util.ShortCodeGenerator;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UrlDetailsService {
    private final UrlDetailsRepository urlDetailsRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UserRepository userRepository;

    public UrlDetailsService(UrlDetailsRepository urlDetailsRepository,
                             ShortCodeGenerator shortCodeGenerator,
                             UserRepository userRepository) {
        this.urlDetailsRepository = urlDetailsRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.userRepository = userRepository;
    }

    public UrlDetails shortenUrl(String longUrl){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to create shortened URLs");
        }

        String username;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            username = ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getName();
        }

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        UrlDetails urlDetails = new UrlDetails();
        urlDetails.setLongUrl(longUrl);
        urlDetails.setUser(user);
        urlDetailsRepository.save(urlDetails);

        String shortUrl = shortCodeGenerator.base62Encode(urlDetails.getId());
        urlDetails.setShortUrl(shortUrl);
        urlDetails.setCreatedAt(LocalDateTime.now());
        urlDetails.setActiveTill(LocalDateTime.now().plusDays(90));
        return urlDetailsRepository.save(urlDetails);
    }

    public UrlDetails changeLongUrl(String shortUrl , String longUrl){
        UrlDetails urlDetails = urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl).orElseThrow(() -> new ResourceNotFoundException("This Short Url does not exist"));
        urlDetails.setLongUrl(longUrl);
        return urlDetailsRepository.save(urlDetails);
    }

    public void deleteUrl(String shortUrl){
        UrlDetails urlDetails = urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl).orElseThrow(() -> new ResourceNotFoundException("This Short Url does not exist"));
        urlDetailsRepository.delete(urlDetails);
    }

    public List<UrlDetails> getAllUrlByLongUrl(String longUrl){
        return new ArrayList<>(urlDetailsRepository.getUrlDetailsByLongUrl(longUrl));
    }

    public String getLongUrlByShortCode(String shortUrl) {

        UrlDetails urlDetails = urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl).orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));
        return urlDetails.getLongUrl();
    }

    public UrlDetails getUrlByShortCode(String shortUrl) {
        return urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl).orElseThrow(() -> new ResourceNotFoundException("Short URL not found"));
    }

    @Transactional
    public void incrementClickCounter (String shortUrl) {
        urlDetailsRepository.incrementClickCounter(shortUrl);
    }

}
