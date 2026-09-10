package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated list of Google Postmaster spam reports.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListGooglePostmasterSpamReportsResponse(
    @JsonProperty("spam_reports") List<GooglePostmasterSpamReport> spamReports,
    @JsonProperty("next_cursor") String nextCursor
) {
    public ListGooglePostmasterSpamReportsResponse {
        if (spamReports == null) {
            spamReports = List.of();
        }
    }
}
