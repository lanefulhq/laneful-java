package com.laneful.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Paginated list of Microsoft SNDS reports.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListSndsReportsResponse(
    @JsonProperty("snds_reports") List<SndsReport> sndsReports,
    @JsonProperty("next_cursor") String nextCursor
) {
    public ListSndsReportsResponse {
        if (sndsReports == null) {
            sndsReports = List.of();
        }
    }
}
