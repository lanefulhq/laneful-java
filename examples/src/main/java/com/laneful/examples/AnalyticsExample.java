package com.laneful.examples;

import com.laneful.client.LanefulClient;
import com.laneful.exceptions.ApiException;
import com.laneful.exceptions.HttpException;
import com.laneful.models.ListDomainSpamRatioRadarParams;
import com.laneful.models.ListGooglePostmasterSpamReportsParams;
import com.laneful.models.ListSndsReportsParams;

import java.time.LocalDate;
import java.util.List;

public class AnalyticsExample {
    public static void main(String[] args) {
        String baseUrl = firstEnv("LANEFUL_ORG_BASE_URL", "LANEFUL_BASE_URL", "https://api.laneful.net");
        String authToken = System.getenv("LANEFUL_AUTH_TOKEN");

        if (authToken == null) {
            System.err.println("Missing LANEFUL_AUTH_TOKEN");
            System.exit(1);
        }

        try {
            LanefulClient client = new LanefulClient(baseUrl, authToken);
            String start = LocalDate.now().minusDays(7).toString();
            String end = LocalDate.now().toString();

            var radar = client.listDomainSpamRatioRadar(new ListDomainSpamRatioRadarParams(
                List.of(), null, start, end, null, null
            ));
            radar.radar().forEach(entry ->
                System.out.println(entry.date() + " " + entry.domain() + " @" + entry.esp() + ": " + entry.spamRatio() + "%")
            );

            var postmaster = client.listGooglePostmasterSpamReports(
                new ListGooglePostmasterSpamReportsParams(List.of(), "example.com", null, null, null, null)
            );
            postmaster.spamReports().forEach(report ->
                System.out.println(report.date() + " " + report.domain() + ": " + report.spamRatio() + "%")
            );

            var snds = client.listSndsReports(new ListSndsReportsParams(null, null, null, null, null));
            snds.sndsReports().forEach(report ->
                System.out.println(report.date() + " " + report.ip() + ": filter=" + report.filterResult()
                    + " complaint=" + report.complaintRate() + "%")
            );
        } catch (ApiException | HttpException e) {
            System.err.println("Analytics API error: " + e.getMessage());
        }
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
