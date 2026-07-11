package com.enterprise.docai.domain.model;

import java.util.Map;

public record SearchQuery(
    String queryText,
    Map<String, Object> filters,
    int limit
) {
    public SearchQuery {
        if (queryText == null || queryText.trim().isEmpty()) {
            throw new IllegalArgumentException("Query text cannot be empty.");
        }
        if (limit <= 0) {
            limit = 10;
        }
    }
}
