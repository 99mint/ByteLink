package com.mint.bytelink.dto.analytics;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsDTO {

    private Long totalClicks;

    private List<CountryClicks> topCountries;

    private List<DeviceClicks> topDevices;

}
