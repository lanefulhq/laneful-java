package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An unsubscribe group in a workspace.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UnsubscribeGroup(
    @JsonProperty("unsubscribe_group_id") long unsubscribeGroupId,
    @JsonProperty("name") String name,
    @JsonProperty("created_at") long createdAt
) {
}
