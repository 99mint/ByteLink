package com.mint.bytelink.util;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

@Component
public class UrlValidator {
    private static final Set<String> BLOCKED_PROTOCOLS = Set.of("javascript", "data", "file");
    private static final Set<String> ALLOWED_PROTOCOLS = Set.of("http", "https");

    public boolean isValid(String url) throws URISyntaxException {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || !ALLOWED_PROTOCOLS.contains(scheme.toLowerCase())) {
                return false;
            }
            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                return false;
            }
            if (url.contains("@") || url.contains("..")) {
                return false;
            }
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
