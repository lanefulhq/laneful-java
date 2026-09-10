package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A daily Microsoft SNDS report for a sending IP.
 * Filter result is one of GREEN, YELLOW, RED, or empty when unknown.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SndsReport(
    @JsonProperty("ip") String ip,
    @JsonProperty("date") String date,
    @JsonProperty("rcpt_commands") long rcptCommands,
    @JsonProperty("data_commands") long dataCommands,
    @JsonProperty("message_recipients") long messageRecipients,
    @JsonProperty("filter_result") String filterResult,
    @JsonProperty("complaint_rate") double complaintRate,
    @JsonProperty("trap_hits") long trapHits
) {
    public static final String FILTER_UNKNOWN = "";
    public static final String FILTER_GREEN = "GREEN";
    public static final String FILTER_YELLOW = "YELLOW";
    public static final String FILTER_RED = "RED";
}
