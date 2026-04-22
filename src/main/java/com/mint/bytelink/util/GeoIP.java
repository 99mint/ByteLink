package com.mint.bytelink.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.InetAddress;

@Slf4j
@Component
public class GeoIP {

    private DatabaseReader reader;

    public GeoIP() {
        try {
            InputStream is = new ClassPathResource("geoip/GeoLite2-Country.mmdb").getInputStream();
            reader = new DatabaseReader.Builder(is).build();
            log.info("GeoIP database loaded successfully");
        } catch (Exception e) {
            log.warn("GeoIP database not found at classpath:geoip/GeoLite2-Country.mmdb. " +
                    "Country lookups will return 'Unknown'. Error: {}", e.getMessage());
            reader = null;
        }
    }

    public String getCountry(String ip) {

        if (reader == null) {
            return "Unknown";
        }

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);

            CountryResponse response = reader.country(ipAddress);

            return response.getCountry().getName();

        } catch (Exception e) {
            return "Unknown";
        }
    }
}
