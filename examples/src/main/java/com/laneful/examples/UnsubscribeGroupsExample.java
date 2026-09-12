package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.models.ListUnsubscribeGroupsParams;
import com.laneful.models.UnsubscribeGroup;

public class UnsubscribeGroupsExample {
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

            UnsubscribeGroup created = client.createUnsubscribeGroup(workspaceId, "Newsletters");
            System.out.println("Created group " + created.unsubscribeGroupId() + ": " + created.name());

            UnsubscribeGroup updated = client.updateUnsubscribeGroup(
                workspaceId,
                created.unsubscribeGroupId(),
                "Weekly Newsletters"
            );
            System.out.println("Updated name: " + updated.name());

            var list = client.listUnsubscribeGroups(workspaceId, new ListUnsubscribeGroupsParams(null, 50, null));
            for (UnsubscribeGroup group : list.unsubscribeGroups()) {
                System.out.println("- " + group.unsubscribeGroupId() + " " + group.name());
            }
        } catch (ApiException | HttpException e) {
            System.err.println("Unsubscribe group API error: " + e.getMessage());
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
