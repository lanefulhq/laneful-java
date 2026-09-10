package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated list of sending domains.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListDomainsResponse(
    @JsonProperty("domains") List<Domain> domains,
    @JsonProperty("pagination") DomainsPagination pagination
) {
    public ListDomainsResponse {
        if (domains == null) {
            domains = List.of();
        }
    }

    @JsonIgnore
    public String nextCursor() {
        return pagination == null ? null : pagination.nextCursor();
    }
}
