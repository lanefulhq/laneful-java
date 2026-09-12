package com.laneful.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query parameters for listing Microsoft SNDS reports.
 * Dates are UTC calendar days in YYYY-MM-DD format.
 */
public record ListSndsReportsParams(
    String ip,
    String startDate,
    String endDate,
    String cursor,
    Integer limit
) {
    public List<Map.Entry<String, String>> toQuery() {
        List<Map.Entry<String, String>> query = new ArrayList<>();
        if (ip != null && !ip.isEmpty()) {
            query.add(Map.entry("ip", ip));
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
