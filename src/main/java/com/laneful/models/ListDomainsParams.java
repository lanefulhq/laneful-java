package com.laneful.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query parameters for listing domains.
 */
public record ListDomainsParams(String cursor, Integer limit, String filterDomain) {
    public List<Map.Entry<String, String>> toQuery() {
        List<Map.Entry<String, String>> query = new ArrayList<>();
        if (cursor != null && !cursor.isEmpty()) {
            query.add(Map.entry("cursor", cursor));
        }
        if (limit != null && limit > 0) {
            query.add(Map.entry("limit", limit.toString()));
        }
        if (filterDomain != null && !filterDomain.isEmpty()) {
            query.add(Map.entry("filter[domain]", filterDomain));
        }
        return query;
    }
}
