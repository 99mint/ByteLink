package com.mint.bytelink.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceClicks {
    private String device;

    private Long clicks;
}
