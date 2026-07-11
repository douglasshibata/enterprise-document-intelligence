package com.enterprise.docai.domain.port;

import com.enterprise.docai.domain.model.Document;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepositoryPort {
    void save(Document document);
    Optional<Document> findById(UUID id);
}
