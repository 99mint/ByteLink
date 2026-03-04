package com.mint.bytelink.service;

import com.mint.bytelink.dto.analytics.AnalyticsDTO;
import com.mint.bytelink.dto.analytics.CountryClicks;
import com.mint.bytelink.dto.analytics.DeviceClicks;
import com.mint.bytelink.entity.Click;
import com.mint.bytelink.entity.UrlDetails;
import com.mint.bytelink.exception.other.ResourceNotFoundException;
import com.mint.bytelink.repository.ClickRepository;
import com.mint.bytelink.repository.UrlDetailsRepository;
import com.mint.bytelink.util.GeoIP;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClickService {

    private final ClickRepository clickRepository;
    private final UrlDetailsRepository urlDetailsRepository;
    private final GeoIP geoIPService;

    public ClickService(ClickRepository clickRepository,
                        UrlDetailsRepository urlDetailsRepository,
                        GeoIP geoIPService) {
        this.clickRepository = clickRepository;
        this.urlDetailsRepository = urlDetailsRepository;
        this.geoIPService = geoIPService;
    }

    public void recordClick(UrlDetails urlDetails,
                            HttpServletRequest request) {

        log.debug("Recording click for shortUrl: {}",
                urlDetails.getShortUrl());

        Click click = new Click();
        click.setClickedAt(LocalDateTime.now());
        click.setUserAgent(request.getHeader("User-Agent"));
        click.setUrlDetails(urlDetails);
        click.setIp(request.getRemoteAddr());
        click.setCountry(geoIPService.getCountry(request.getRemoteAddr()));
        click.setDevice(getDeviceType(request.getHeader("User-Agent")));

        clickRepository.save(click);

        log.debug("Click recorded successfully for shortUrl: {}",
                urlDetails.getShortUrl());
    }

    public AnalyticsDTO provideAnalytics(String shortUrl){
        if (!urlDetailsRepository.existsUrlDetailsByShortUrl(shortUrl)){
            throw new ResourceNotFoundException("Short url doesn't exist");
        }

        log.info("Providing analytics for short url {}", shortUrl);
        AnalyticsDTO analytics = new AnalyticsDTO();

        analytics.setTotalClicks(urlDetailsRepository.totalClicksByShortUrl(shortUrl));
        analytics.setTopDevices(topDevices(shortUrl));
        analytics.setTopCountries(topCountries(shortUrl));

        log.info("Returning analytics for short url : {}" , shortUrl);
        return analytics;
    }

    public List<CountryClicks> topCountries(String shortUrl){
        return clickRepository.getCountryFrequency(shortUrl);
    }

    public List<DeviceClicks> topDevices(String shortUrl){
        return clickRepository.getDeviceFrequency(shortUrl);
    }

    public String getDeviceType(String userAgent) {

        if (userAgent == null) {
            return "Unknown";
        }

        userAgent = userAgent.toLowerCase();

        if (userAgent.contains("mobile") ||
                userAgent.contains("android") ||
                userAgent.contains("iphone")) {
            return "Mobile";
        }

        if (userAgent.contains("ipad") ||
                userAgent.contains("tablet")) {
            return "Tablet";
        }

        return "Desktop";
    }
}
