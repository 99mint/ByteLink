package com.mint.bytelink.util;

import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {

    private static final String base62Encoder =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String base62Encode(Long id) {

        if (id == 0) return "000000"; // fixed length zero

        StringBuilder shortCode = new StringBuilder();

        while (id > 0) {
            int remainder = (int) (id % 62);
            shortCode.append(base62Encoder.charAt(remainder));
            id = id / 62;
        }

        String encoded = shortCode.reverse().toString();

        // PAD TO 6 CHARACTERS
        return String.format("%6s", encoded).replace(' ', '0');
    }
}

