package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/**
 * Configuration for email tracking settings.
 */
public record TrackingSettings(
    @JsonProperty("opens") boolean opens,
    @JsonProperty("clicks") boolean clicks,
    @JsonProperty("unsubscribes") boolean unsubscribes
) {
    
    /**
     * Creates tracking settings from a map representation.
     * 
     * @param data Map containing tracking settings
     * @return New TrackingSettings instance
     */
    public static TrackingSettings fromMap(java.util.Map<String, Object> data) {
        boolean opens = (Boolean) data.getOrDefault("opens", false);
        boolean clicks = (Boolean) data.getOrDefault("clicks", false);
        boolean unsubscribes = (Boolean) data.getOrDefault("unsubscribes", false);
        return new TrackingSettings(opens, clicks, unsubscribes);
    }
}
