package com.laneful.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query parameters for listing domain spam-ratio radar entries.
 * Dates are UTC calendar days in YYYY-MM-DD format.
 */
public record ListDomainSpamRatioRadarParams(
    List<Long> workspaceIds,
    String domain,
    String startDate,
    String endDate,
    String cursor,
    Integer limit
) {
    public ListDomainSpamRatioRadarParams {
        if (workspaceIds == null) {
            workspaceIds = List.of();
        }
    }

    public List<Map.Entry<String, String>> toQuery() {
        List<Map.Entry<String, String>> query = new ArrayList<>();
        for (Long id : workspaceIds) {
            query.add(Map.entry("workspace_ids", id.toString()));
        }
        if (domain != null && !domain.isEmpty()) {
            query.add(Map.entry("domain", domain));
        }
        if (startDate != null && !startDate.isEmpty()) {
            query.add(Map.entry("start_date", startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            query.add(Map.entry("end_date", endDate));
        }
        if (cursor != null && !cursor.isEmpty()) {
            query.add(Map.entry("cursor", cursor));
        }
        if (limit != null && limit > 0) {
            query.add(Map.entry("limit", limit.toString()));
        }
        return query;
    }
}
