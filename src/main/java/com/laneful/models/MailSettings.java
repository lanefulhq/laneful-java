package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request-level mail settings (sandbox mode, return message IDs).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailSettings(
    @JsonProperty("sandbox_mode") Boolean sandboxMode,
    @JsonProperty("return_message_ids") Boolean returnMessageIds
) {
    public MailSettings() {
        this(null, null);
    }
}
