package com.laneful.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laneful.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ModelTests {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void fromHeaderSerializesAsFromHeader() throws Exception {
        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("to@example.com"))
            .subject("Test")
            .textContent("Content")
            .fromHeader(new Address("newsletter@example.com", "Newsletter"))
            .build();

        assertEquals("newsletter@example.com", email.getFromHeader().email());
        String json = mapper.writeValueAsString(email);
        assertTrue(json.contains("\"from_header\""));
        assertTrue(json.contains("newsletter@example.com"));
        assertFalse(json.contains("\"cc\""));
        assertFalse(json.contains("\"bcc\""));
        assertFalse(json.contains("\"attachments\""));
        assertFalse(json.contains("fromHeader"));
    }

    @Test
    void webhookDataAllowsTwentyKeys() throws ValidationException {
        Map<String, String> data = IntStream.range(0, 20)
            .boxed()
            .collect(Collectors.toMap(i -> "key" + i, i -> "value" + i, (a, b) -> a, LinkedHashMap::new));

        Email email = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("to@example.com"))
            .subject("Test")
            .textContent("Content")
            .webhookData(data)
            .build();

        assertEquals(20, email.getWebhookData().size());
    }

    @Test
    void webhookDataRejectsTwentyOneKeys() {
        Map<String, String> data = IntStream.range(0, 21)
            .boxed()
            .collect(Collectors.toMap(i -> "key" + i, i -> "value" + i));

        ValidationException ex = assertThrows(ValidationException.class, () ->
            new Email.Builder()
                .from(new Address("sender@example.com"))
                .to(new Address("to@example.com"))
                .subject("Test")
                .textContent("Content")
                .webhookData(data)
                .build()
        );
        assertTrue(ex.getMessage().contains("20 keys"));
    }

    // "é" is 2 bytes in UTF-8: 25 fill a 50-byte key and 50 fill a 100-byte value.
    @ParameterizedTest
    @CsvSource(value = {"25, 50, NONE", "26, 1, 50 bytes", "1, 51, 100 bytes"})
    void webhookDataCountsUtf8Bytes(int keyChars, int valueChars, String expectedError) throws Exception {
        Map<String, String> data = Map.of("é".repeat(keyChars), "é".repeat(valueChars));
        Email.Builder builder = new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("to@example.com"))
            .subject("Test")
            .textContent("Content")
            .webhookData(data);

        if (expectedError.equals("NONE")) {
            assertEquals(data, builder.build().getWebhookData());
        } else {
            ValidationException ex = assertThrows(ValidationException.class, builder::build);
            assertTrue(ex.getMessage().contains(expectedError), ex.getMessage());
        }
    }

    @Test
    void trackingSettingsSerializesUnsubscribeGroupName() throws Exception {
        TrackingSettings tracking = new TrackingSettings(true, false, true, null, "Newsletters");
        String json = mapper.writeValueAsString(tracking);
        assertTrue(json.contains("\"unsubscribe_group_name\":\"Newsletters\""));
        assertFalse(json.contains("unsubscribe_group_id"));
        assertTrue(json.contains("\"clicks\":false"));
    }

    @Test
    void mailSettingsOmitsNullsAndKeepsFalse() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new MailSettings()));

        String json = mapper.writeValueAsString(new MailSettings(true, false));
        assertTrue(json.contains("\"sandbox_mode\":true"));
        assertTrue(json.contains("\"return_message_ids\":false"));
        assertFalse(json.contains("idempotency_key"));
    }

    @Test
    void addressWithoutNameOmitsName() throws Exception {
        // The API rejects "name": null with "JSON data error".
        assertEquals("{\"email\":\"user@example.com\"}", mapper.writeValueAsString(new Address("user@example.com")));
        assertEquals("{\"email\":\"user@example.com\",\"name\":\"User\"}",
            mapper.writeValueAsString(new Address("user@example.com", "User")));
    }

    @Test
    void mailSettingsIdempotencyKey() throws Exception {
        String json = mapper.writeValueAsString(new MailSettings().withIdempotencyKey("order-1234"));
        assertEquals("{\"idempotency_key\":\"order-1234\"}", json);

        new MailSettings(null, null, "k".repeat(36));
        for (String key : new String[] {"", "k".repeat(37), "é".repeat(19)}) {
            assertThrows(IllegalArgumentException.class, () -> new MailSettings(null, null, key));
        }
    }

    @Test
    void updateDomainRequestThreeWayTrack() throws Exception {
        assertEquals("{}", mapper.writeValueAsString(new UpdateDomainRequest()));
        assertTrue(mapper.writeValueAsString(new UpdateDomainRequest("")).contains("\"email_track_id\":\"\""));
        assertTrue(mapper.writeValueAsString(new UpdateDomainRequest("track-1")).contains("\"email_track_id\":\"track-1\""));
    }
}
