package com.enterprise.docai.domain.model;

import java.util.List;

public record SearchResult(
    String queryText,
    List<ScoredDocumentChunk> results
) {}
