package com.laneful.models;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request-level mail settings (sandbox mode, return message IDs, idempotency key).
 *
 * <p>{@code idempotencyKey} protects against duplicate sends: Laneful accepts a key once per
 * organization for one hour. A repeated request with the same key is answered with status
 * "accepted" (and no message IDs) and sends nothing. Keys are 1 to 36 bytes.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MailSettings(
    @JsonProperty("sandbox_mode") Boolean sandboxMode,
    @JsonProperty("return_message_ids") Boolean returnMessageIds,
    @JsonProperty("idempotency_key") String idempotencyKey
) {
    /**
     * @throws IllegalArgumentException If idempotencyKey is set and not 1 to 36 bytes long
     */
    public MailSettings {
        if (idempotencyKey != null) {
            int length = idempotencyKey.getBytes(StandardCharsets.UTF_8).length;
            if (length < 1 || length > 36) {
                throw new IllegalArgumentException("idempotencyKey must contain between 1 and 36 bytes");
            }
        }
    }

    public MailSettings(Boolean sandboxMode, Boolean returnMessageIds) {
        this(sandboxMode, returnMessageIds, null);
    }

    public MailSettings() {
        this(null, null, null);
    }

    /**
     * Returns a copy of these settings with the given idempotency key.
     */
    public MailSettings withIdempotencyKey(String idempotencyKey) {
        return new MailSettings(sandboxMode, returnMessageIds, idempotencyKey);
    }
}
