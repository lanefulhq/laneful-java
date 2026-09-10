package com.laneful.client;

import com.laneful.exceptions.ValidationException;
import com.laneful.models.Address;
import com.laneful.models.CreateDomainRequest;
import com.laneful.models.Domain;
import com.laneful.models.Email;
import com.laneful.models.ListDomainSpamRatioRadarParams;
import com.laneful.models.ListDomainsParams;
import com.laneful.models.ListGooglePostmasterSpamReportsParams;
import com.laneful.models.ListSndsReportsParams;
import com.laneful.models.ListUnsubscribeGroupsParams;
import com.laneful.models.MailSettings;
import com.laneful.models.SndsReport;
import com.laneful.models.UpdateDomainRequest;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LanefulClientTest {

    private MockWebServer server;
    private LanefulClient client;

    @BeforeEach
    void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        client = new LanefulClient(
            server.url("/").toString(),
            "test-token",
            Duration.ofSeconds(5),
            new OkHttpClient()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void testClientCreationWithEmptyBaseUrl() {
        assertThrows(ValidationException.class, () -> new LanefulClient("", "test-token"));
    }

    @Test
    void testClientCreationWithEmptyAuthToken() {
        assertThrows(ValidationException.class, () -> new LanefulClient("https://test.send.laneful.net", ""));
    }

    @Test
    void sendEmailIncludesMailSettings() throws Exception {
        server.enqueue(new MockResponse()
            .setBody("{\"status\":\"accepted\",\"message_ids\":[\"msg-1\"]}")
            .setHeader("Content-Type", "application/json"));

        Map<String, Object> response = client.sendEmail(sampleEmail(), new MailSettings(true, true));
        RecordedRequest request = server.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/v1/email/send", request.getPath());
        assertTrue(request.getBody().readUtf8().contains("\"sandbox_mode\":true"));
        assertEquals("accepted", response.get("status"));
        assertEquals("laneful-java/1.2.0", request.getHeader("User-Agent"));
        assertEquals("application/json", request.getHeader("Content-Type"));
    }

    @Test
    void listUnsubscribeGroupsBuildsQuery() throws Exception {
        server.enqueue(new MockResponse()
            .setBody("{\"unsubscribe_groups\":[{\"unsubscribe_group_id\":9,\"name\":\"Newsletters\",\"created_at\":1710000000}],\"next_cursor\":\"abc\"}")
            .setHeader("Content-Type", "application/json"));

        var result = client.listUnsubscribeGroups(42, new ListUnsubscribeGroupsParams(null, 10, "news"));
        RecordedRequest request = server.takeRequest();

        assertEquals("/v1/workspaces/42/unsubscribe-groups?limit=10&search=news", request.getPath());
        assertEquals("Newsletters", result.unsubscribeGroups().get(0).name());
        assertEquals("abc", result.nextCursor());
    }

    @Test
    void createAndUpdateUnsubscribeGroup() throws Exception {
        server.enqueue(json("{\"unsubscribe_group\":{\"unsubscribe_group_id\":9,\"name\":\"Newsletters\",\"created_at\":1710000000}}"));
        server.enqueue(json("{\"unsubscribe_group\":{\"unsubscribe_group_id\":9,\"name\":\"Promos\",\"created_at\":1710000000}}"));

        var created = client.createUnsubscribeGroup(42, "Newsletters");
        assertEquals(9, created.unsubscribeGroupId());
        assertEquals("POST", server.takeRequest().getMethod());

        var updated = client.updateUnsubscribeGroup(42, 9, "Promos");
        assertEquals("Promos", updated.name());
        assertEquals("PATCH", server.takeRequest().getMethod());
    }

    @Test
    void domainEndpoints() throws Exception {
        String domainJson = "{\"domain\":\"example.com\",\"tracking\":\"track\",\"return_path\":\"bounce\",\"verified\":true,\"dmarc_verified\":true,\"email_track_id\":\"track-1\"}";
        server.enqueue(json("{\"domains\":[" + domainJson + "],\"pagination\":{\"next_cursor\":null}}"));
        server.enqueue(json(domainJson));
        server.enqueue(json(domainJson));
        server.enqueue(json(domainJson));
        server.enqueue(json(domainJson));
        server.enqueue(json("{\"message\":\"deleted\"}"));

        var list = client.listDomains(42, new ListDomainsParams(null, null, "example.com"));
        assertEquals("example.com", list.domains().get(0).domain());
        assertTrue(server.takeRequest().getPath().contains("filter%5Bdomain%5D=example.com"));

        Domain got = client.getDomain(42, "example.com");
        assertTrue(got.dmarcVerified());
        server.takeRequest();

        Domain created = client.createDomain(42, new CreateDomainRequest("example.com", "track", "bounce"));
        assertEquals("example.com", created.domain());
        server.takeRequest();

        Domain updated = client.updateDomain(42, "example.com", new UpdateDomainRequest("track-1"));
        assertEquals("track-1", updated.emailTrackId());
        server.takeRequest();

        Domain verified = client.verifyDomain(42, "example.com");
        assertTrue(verified.verified());
        server.takeRequest();

        assertEquals("deleted", client.deleteDomain(42, "example.com").message());
        assertEquals("DELETE", server.takeRequest().getMethod());
    }

    @Test
    void analyticsEndpoints() throws Exception {
        server.enqueue(json("{\"radar\":[{\"workspace_id\":1,\"domain\":\"example.com\",\"esp\":\"Gmail\",\"spam_ratio\":0.2,\"date\":\"2026-09-01\"}]}"));
        server.enqueue(json("{\"spam_reports\":[{\"workspace_id\":1,\"domain\":\"example.com\",\"date\":\"2026-09-01\",\"spam_ratio\":0.01}]}"));
        server.enqueue(json("{\"snds_reports\":[{\"ip\":\"203.0.113.5\",\"date\":\"2026-09-01\",\"rcpt_commands\":1,\"data_commands\":1,\"message_recipients\":1,\"filter_result\":\"GREEN\",\"complaint_rate\":0,\"trap_hits\":0}]}"));

        var radar = client.listDomainSpamRatioRadar(new ListDomainSpamRatioRadarParams(
            List.of(1L, 2L), null, null, null, null, null
        ));
        RecordedRequest radarReq = server.takeRequest();
        assertTrue(radarReq.getPath().contains("workspace_ids=1"));
        assertTrue(radarReq.getPath().contains("workspace_ids=2"));
        assertEquals("Gmail", radar.radar().get(0).esp());

        var postmaster = client.listGooglePostmasterSpamReports(new ListGooglePostmasterSpamReportsParams(
            List.of(), null, null, null, null, null
        ));
        server.takeRequest();
        assertEquals("example.com", postmaster.spamReports().get(0).domain());

        var snds = client.listSndsReports(new ListSndsReportsParams("203.0.113.5", null, null, null, null));
        RecordedRequest sndsReq = server.takeRequest();
        assertTrue(sndsReq.getPath().contains("/analytics/microsoft-snds/reports"));
        assertTrue(sndsReq.getPath().contains("ip=203.0.113.5"));
        assertEquals(SndsReport.FILTER_GREEN, snds.sndsReports().get(0).filterResult());
    }

    private static Email sampleEmail() throws ValidationException {
        return new Email.Builder()
            .from(new Address("sender@example.com"))
            .to(new Address("recipient@example.com"))
            .subject("Test")
            .textContent("Content")
            .build();
    }

    private static MockResponse json(String body) {
        return new MockResponse().setBody(body).setHeader("Content-Type", "application/json");
    }
}
