package com.enterprise.docai.domain.model;

import java.util.UUID;

public record ScoredDocumentChunk(
    UUID chunkId,
    UUID documentId,
    String documentTitle,
    String text,
    double score
) {}
