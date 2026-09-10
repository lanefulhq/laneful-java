package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.models.CreateDomainRequest;
import com.laneful.models.Domain;
import com.laneful.models.ListDomainsParams;
import com.laneful.models.UpdateDomainRequest;

public class DomainsExample {
    public static void main(String[] args) {
        String baseUrl = firstEnv("LANEFUL_ORG_BASE_URL", "LANEFUL_BASE_URL", "https://api.laneful.net");
        String authToken = System.getenv("LANEFUL_AUTH_TOKEN");
        long workspaceId = Long.parseLong(envOr("LANEFUL_WORKSPACE_ID", "1"));

        if (authToken == null) {
            System.err.println("Missing LANEFUL_AUTH_TOKEN");
            System.exit(1);
        }

        try {
            LanefulClient client = new LanefulClient(baseUrl, authToken);

            var list = client.listDomains(workspaceId, new ListDomainsParams(null, 50, null));
            System.out.println("Domains: " + list.domains().size());

            Domain domain = client.createDomain(workspaceId, new CreateDomainRequest(
                "mydomain.com",
                "tracking",
                "return-path"
            ));
            System.out.println("Created " + domain.domain() + ", verified=" + domain.verified());

            domain = client.verifyDomain(workspaceId, "mydomain.com");
            System.out.println("Verification: dmarc=" + domain.dmarcVerified());

            domain = client.updateDomain(
                workspaceId,
                "mydomain.com",
                new UpdateDomainRequest("e59f0a35-05bc-4516-b585-c06f69c3e67e")
            );
            System.out.println("Email track: " + domain.emailTrackId());
        } catch (ApiException | HttpException e) {
            System.err.println("Domain API error: " + e.getMessage());
        }
    }

    private static String envOr(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isEmpty() ? fallback : value;
    }

    private static String firstEnv(String first, String second, String fallback) {
        String value = System.getenv(first);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        value = System.getenv(second);
        return value == null || value.isEmpty() ? fallback : value;
    }
}
