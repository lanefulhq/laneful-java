package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A sending domain whose spam complaint ratio reached a critical level
 * at a mailbox provider on a given day.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DomainSpamRatioRadar(
    @JsonProperty("workspace_id") long workspaceId,
    @JsonProperty("domain") String domain,
    @JsonProperty("esp") String esp,
    @JsonProperty("spam_ratio") double spamRatio,
    @JsonProperty("date") String date
) {
}
