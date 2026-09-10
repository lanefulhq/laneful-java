package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A sending domain and its verification state.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Domain(
    @JsonProperty("domain") String domain,
    @JsonProperty("tracking") String tracking,
    @JsonProperty("return_path") String returnPath,
    @JsonProperty("verified") boolean verified,
    @JsonProperty("tracking_verified") boolean trackingVerified,
    @JsonProperty("return_path_verified") boolean returnPathVerified,
    @JsonProperty("dkim1_verified") boolean dkim1Verified,
    @JsonProperty("dkim2_verified") boolean dkim2Verified,
    @JsonProperty("dmarc_verified") boolean dmarcVerified,
    @JsonProperty("require_tls") boolean requireTls,
    @JsonProperty("email_track_id") String emailTrackId
) {
}
