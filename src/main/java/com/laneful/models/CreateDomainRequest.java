package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for creating a sending domain.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateDomainRequest(
    @JsonProperty("domain") String domain,
    @JsonProperty("tracking") String tracking,
    @JsonProperty("return_path") String returnPath,
    @JsonProperty("require_tls") Boolean requireTls,
    @JsonProperty("email_track_id") String emailTrackId
) {
    public CreateDomainRequest(String domain, String tracking, String returnPath) {
        this(domain, tracking, returnPath, null, null);
    }
}
