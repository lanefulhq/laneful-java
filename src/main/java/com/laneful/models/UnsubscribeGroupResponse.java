package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API envelope for a single unsubscribe group.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UnsubscribeGroupResponse(
    @JsonProperty("unsubscribe_group") UnsubscribeGroup unsubscribeGroup
) {
}
