package com.mint.bytelink.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CountryClicks {
    private String country;

    private Long clicks;
}
