package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for updating a domain's mutable settings.
 * Pass a track ID to set the email track, an empty string to clear it
 * (the domain falls back to the default track), or null to leave it unchanged.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateDomainRequest(
    @JsonProperty("email_track_id") String emailTrackId
) {
    public UpdateDomainRequest() {
        this(null);
    }
}
