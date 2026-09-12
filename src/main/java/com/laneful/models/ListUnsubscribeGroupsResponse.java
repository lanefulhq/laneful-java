package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated list of unsubscribe groups.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListUnsubscribeGroupsResponse(
    @JsonProperty("unsubscribe_groups") List<UnsubscribeGroup> unsubscribeGroups,
    @JsonProperty("next_cursor") String nextCursor
) {
    public ListUnsubscribeGroupsResponse {
        if (unsubscribeGroups == null) {
            unsubscribeGroups = List.of();
        }
    }
}
