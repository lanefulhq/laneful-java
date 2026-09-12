package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated list of domain spam-ratio radar entries.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListDomainSpamRatioRadarResponse(
    @JsonProperty("radar") List<DomainSpamRatioRadar> radar,
    @JsonProperty("next_cursor") String nextCursor
) {
    public ListDomainSpamRatioRadarResponse {
        if (radar == null) {
            radar = List.of();
        }
    }
}
