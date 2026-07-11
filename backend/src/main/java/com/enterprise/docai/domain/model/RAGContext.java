package com.enterprise.docai.domain.model;

import java.util.List;

public record RAGContext(
    String question,
    List<ScoredDocumentChunk> referencedChunks,
    String generatedAnswer
) {}
