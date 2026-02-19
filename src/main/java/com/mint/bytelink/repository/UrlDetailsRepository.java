package com.mint.bytelink.repository;

import com.mint.bytelink.entity.UrlDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UrlDetailsRepository extends JpaRepository<UrlDetails , Long> {
    Optional<UrlDetails> getUrlDetailsByShortUrl(String shortUrl);
    List<UrlDetails> getUrlDetailsByLongUrl(String longUrl);
}
