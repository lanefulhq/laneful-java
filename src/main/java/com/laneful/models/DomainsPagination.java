package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pagination details for a domains listing.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DomainsPagination(
    @JsonProperty("next_cursor") String nextCursor
) {
}
