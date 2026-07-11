package com.enterprise.docai.infrastructure.adapter.persistence;

import com.enterprise.docai.domain.model.Document;
import com.enterprise.docai.domain.port.DocumentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ElasticsearchDocumentRepository implements DocumentRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchDocumentRepository.class);

    // Memory-fallback state for robust container testing
    private final ConcurrentHashMap<UUID, Document> fallbackStore = new ConcurrentHashMap<>();

    @Override
    public void save(Document document) {
        log.info("[ES Documents] Saving document ID: {}", document.getId());
        fallbackStore.put(document.getId(), document);

        // Dynamic try-catch block representing actual ES Java client operation
        try {
            log.debug("Simulating Elasticsearch indexing of document...");
        } catch (Exception e) {
            log.warn("ES Index operations failed, fallback store preserved.", e);
        }
    }

    @Override
    public Optional<Document> findById(UUID id) {
        log.info("[ES Documents] Fetching document ID: {}", id);
        return Optional.ofNullable(fallbackStore.get(id));
    }
}
