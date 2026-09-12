package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Configuration for email tracking settings.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrackingSettings(
    @JsonProperty("opens") boolean opens,
    @JsonProperty("clicks") boolean clicks,
    @JsonProperty("unsubscribes") boolean unsubscribes,
    @JsonProperty("unsubscribe_group_id") Long unsubscribeGroupId,
    @JsonProperty("unsubscribe_group_name") String unsubscribeGroupName
) {
    public TrackingSettings(boolean opens, boolean clicks, boolean unsubscribes) {
        this(opens, clicks, unsubscribes, null, null);
    }

    /**
     * Creates tracking settings from a map representation.
     *
     * @param data Map containing tracking settings
     * @return New TrackingSettings instance
     */
    public static TrackingSettings fromMap(Map<String, Object> data) {
        boolean opens = Boolean.TRUE.equals(data.get("opens"));
        boolean clicks = Boolean.TRUE.equals(data.get("clicks"));
        boolean unsubscribes = Boolean.TRUE.equals(data.get("unsubscribes"));
        Long groupId = switch (data.get("unsubscribe_group_id")) {
            case Long l -> l;
            case Integer i -> i.longValue();
            case Number n -> n.longValue();
            default -> null;
        };
        Object name = data.get("unsubscribe_group_name");
        return new TrackingSettings(opens, clicks, unsubscribes, groupId, name == null ? null : name.toString());
    }
}
