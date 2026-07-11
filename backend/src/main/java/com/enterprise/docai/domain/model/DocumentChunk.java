package com.enterprise.docai.domain.model;

import java.util.UUID;

public class DocumentChunk {
    private final UUID id;
    private final UUID documentId;
    private final String text;
    private final double[] embedding;
    private final int chunkIndex;

    public DocumentChunk(UUID id, UUID documentId, String text, double[] embedding, int chunkIndex) {
        if (documentId == null) {
            throw new IllegalArgumentException("Document ID association is required.");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Chunk text cannot be empty.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.documentId = documentId;
        this.text = text;
        this.embedding = embedding;
        this.chunkIndex = chunkIndex;
    }

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public String getText() { return text; }
    public double[] getEmbedding() { return embedding; }
    public int getChunkIndex() { return chunkIndex; }
}
