package com.mint.bytelink.service;

import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.exception.ResourceNotFoundException;
import com.mint.bytelink.repository.UrlDetailsRepository;
import com.mint.bytelink.util.ShortCodeGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UrlDetailsService {
    private final UrlDetailsRepository urlDetailsRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlDetailsService(UrlDetailsRepository urlDetailsRepository , ShortCodeGenerator shortCodeGenerator) {
        this.urlDetailsRepository = urlDetailsRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    public UrlDetails shortenUrl(String longUrl){

        UrlDetails urlDetails = new UrlDetails();
        urlDetails.setLongUrl(longUrl);
        urlDetailsRepository.save(urlDetails);

        String shortUrl = shortCodeGenerator.base62Encode(urlDetails.getId());
        urlDetails.setShortUrl(shortUrl);
        urlDetails.setCreatedAt(LocalDateTime.now());
        urlDetails.setExpiration(LocalDateTime.now().plusDays(90));
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

}
