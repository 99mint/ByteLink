package com.mint.bytelink.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;

@Component
public class GeoIP {

    private final DatabaseReader reader;

    public GeoIP() throws Exception {
        File database = new File("src/main/resources/geoip/GeoLite2-Country.mmdb");
        reader = new DatabaseReader.Builder(database).build();
    }

    public String getCountry(String ip) {

        try {
            InetAddress ipAddress = InetAddress.getByName(ip);

            CountryResponse response = reader.country(ipAddress);

            return response.getCountry().getName();

        } catch (Exception e) {
            return "Unknown";
        }
    }
}
