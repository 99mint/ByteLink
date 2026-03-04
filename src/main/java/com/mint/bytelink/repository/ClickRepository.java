package com.mint.bytelink.repository;

import com.mint.bytelink.dto.analytics.CountryClicks;
import com.mint.bytelink.dto.analytics.DeviceClicks;
import com.mint.bytelink.entity.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClickRepository extends JpaRepository<Click, Long> {
    @Query("SELECT c.userAgent FROM Click c WHERE c.urlDetails.shortUrl = :shortUrl")
    List<String> findUserAgentsByShortUrl(String shortUrl);

    @Query("SELECT c.Ip FROM Click c WHERE c.urlDetails.shortUrl = :shortUrl")
    List<String> findIpByShortUrl(String shortUrl);

    @Query("SELECT new com.mint.bytelink.dto.analytics.DeviceClicks(c.device, COUNT(c)) FROM Click c WHERE c.urlDetails.shortUrl = :shortUrl GROUP BY c.device ORDER BY COUNT(c) DESC")
    List<DeviceClicks> getDeviceFrequency(String shortUrl);

    @Query("SELECT new com.mint.bytelink.dto.analytics.CountryClicks(c.country, COUNT(c)) FROM Click c WHERE c.urlDetails.shortUrl = :shortUrl GROUP BY c.country ORDER BY COUNT(c) DESC")
    List<CountryClicks> getCountryFrequency(String shortUrl);

}
