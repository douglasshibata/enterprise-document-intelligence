package com.enterprise.docai.domain.model;

import java.util.Map;
import java.util.UUID;

public class Document {
    private final UUID id;
    private final String title;
    private final String content;
    private final Map<String, Object> metadata;

    public Document(UUID id, String title, String content, Map<String, Object> metadata) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Document title cannot be empty.");
        }
        this.id = id != null ? id : UUID.randomUUID();
        this.title = title;
        this.content = content != null ? content : "";
        this.metadata = metadata != null ? metadata : Map.of();
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Map<String, Object> getMetadata() { return metadata; }
}
