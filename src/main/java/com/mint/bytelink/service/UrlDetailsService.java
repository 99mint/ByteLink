package com.mint.bytelink.service;

import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.entity.User;
import com.mint.bytelink.exception.other.ResourceNotFoundException;
import com.mint.bytelink.exception.other.UrlValidationException;
import com.mint.bytelink.repository.UrlDetailsRepository;
import com.mint.bytelink.repository.UserRepository;
import com.mint.bytelink.security.CustomUserDetails;
import com.mint.bytelink.util.ShortCodeGenerator;
import com.mint.bytelink.util.UrlValidator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UrlDetailsService {

    private final UrlDetailsRepository urlDetailsRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final UserRepository userRepository;
    private final UrlValidator urlValidator;

    public UrlDetailsService(UrlDetailsRepository urlDetailsRepository,
                             ShortCodeGenerator shortCodeGenerator,
                             UserRepository userRepository,
                             UrlValidator urlValidator) {
        this.urlDetailsRepository = urlDetailsRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.userRepository = userRepository;
        this.urlValidator = urlValidator;
    }

    public UrlDetails shortenUrl(String longUrl) throws URISyntaxException {

        log.info("Shorten URL request received for longUrl: {}", longUrl);

        if (!urlValidator.isValid(longUrl)) {
            log.warn("URL validation failed for: {}", longUrl);
            throw new UrlValidationException("Enter a url with http or https protocol");
        }

        String username = getCurrentUser();
        log.debug("Authenticated user '{}' is creating short URL", username);

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.error("Authenticated user not found in DB: {}", username);
                    return new UsernameNotFoundException("User not found");
                });

        UrlDetails urlDetails = new UrlDetails();
        urlDetails.setLongUrl(longUrl);
        urlDetails.setUser(user);
        urlDetailsRepository.save(urlDetails);

        String shortUrl = shortCodeGenerator.base62Encode(urlDetails.getId());
        urlDetails.setShortUrl(shortUrl);
        urlDetails.setCreatedAt(LocalDateTime.now());
        urlDetails.setActiveTill(LocalDateTime.now().plusDays(90));

        UrlDetails saved = urlDetailsRepository.save(urlDetails);

        log.info("Short URL '{}' created successfully for user '{}'",
                shortUrl, username);

        return saved;
    }

    private static String getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("Unauthenticated access attempt while creating shortened URL");
            throw new IllegalStateException("User must be authenticated to create shortened URLs");
        }

        String username;

        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            username =
                    ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        } else {
            username = authentication.getName();
        }

        return username;
    }

    public UrlDetails changeLongUrl(String shortUrl, String longUrl) {

        log.info("Request to update shortUrl: {}", shortUrl);

        UrlDetails urlDetails =
                urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl)
                        .orElseThrow(() -> {
                            log.warn("Update failed - shortUrl not found: {}", shortUrl);
                            return new ResourceNotFoundException("This Short Url does not exist");
                        });

        urlDetails.setLongUrl(longUrl);

        UrlDetails updated = urlDetailsRepository.save(urlDetails);

        log.info("ShortUrl '{}' updated successfully", shortUrl);

        return updated;
    }

    public void deleteUrl(String shortUrl) {

        log.info("Request to delete shortUrl: {}", shortUrl);

        UrlDetails urlDetails =
                urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl)
                        .orElseThrow(() -> {
                            log.warn("Delete failed - shortUrl not found: {}", shortUrl);
                            return new ResourceNotFoundException("This Short Url does not exist");
                        });

        urlDetailsRepository.delete(urlDetails);

        log.info("ShortUrl '{}' deleted successfully", shortUrl);
    }

    public List<UrlDetails> getAllUrlByLongUrl(String longUrl) {

        log.debug("Fetching URLs for longUrl: {}", longUrl);

        List<UrlDetails> urls =
                new ArrayList<>(urlDetailsRepository.getUrlDetailsByLongUrl(longUrl));

        log.debug("Found {} entries for longUrl: {}", urls.size(), longUrl);

        return urls;
    }

    public String getLongUrlByShortCode(String shortUrl) {

        log.debug("Fetching longUrl for shortUrl: {}", shortUrl);

        UrlDetails urlDetails =
                urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl)
                        .orElseThrow(() -> {
                            log.warn("ShortUrl not found: {}", shortUrl);
                            return new ResourceNotFoundException("Short URL not found");
                        });

        return urlDetails.getLongUrl();
    }

    public UrlDetails getUrlByShortCode(String shortUrl) {

        log.debug("Fetching UrlDetails for shortUrl: {}", shortUrl);

        return urlDetailsRepository.getUrlDetailsByShortUrl(shortUrl)
                .orElseThrow(() -> {
                    log.warn("ShortUrl not found: {}", shortUrl);
                    return new ResourceNotFoundException("Short URL not found");
                });
    }

    @Transactional
    public void incrementClickCounter(String shortUrl) {

        log.debug("Incrementing click counter for shortUrl: {}", shortUrl);

        urlDetailsRepository.incrementClickCounter(shortUrl);
    }
}
