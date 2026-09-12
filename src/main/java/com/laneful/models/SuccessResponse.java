package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic success message returned by mutating endpoints that do not return a resource body.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SuccessResponse(
    @JsonProperty("message") String message
) {
}
