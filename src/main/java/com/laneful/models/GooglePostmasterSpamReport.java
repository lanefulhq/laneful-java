package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A daily Gmail spam-rate report from Google Postmaster Tools.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GooglePostmasterSpamReport(
    @JsonProperty("workspace_id") long workspaceId,
    @JsonProperty("domain") String domain,
    @JsonProperty("date") String date,
    @JsonProperty("spam_ratio") double spamRatio
) {
}
