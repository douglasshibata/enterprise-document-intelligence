package com.enterprise.docai.application.usecase;

import com.enterprise.docai.domain.model.Document;
import com.enterprise.docai.domain.model.DocumentChunk;
import com.enterprise.docai.domain.port.ChunkRepositoryPort;
import com.enterprise.docai.domain.port.DocumentEmbeddingPort;
import com.enterprise.docai.domain.port.DocumentRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IngestDocumentUseCase {
    private static final Logger log = LoggerFactory.getLogger(IngestDocumentUseCase.class);

    private final DocumentRepositoryPort documentRepository;
    private final ChunkRepositoryPort chunkRepository;
    private final DocumentEmbeddingPort embeddingPort;
    private static final int CHUNK_SIZE = 512;
    private static final int CHUNK_OVERLAP = 64;

    public IngestDocumentUseCase(DocumentRepositoryPort documentRepository,
                                 ChunkRepositoryPort chunkRepository,
                                 DocumentEmbeddingPort embeddingPort) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.embeddingPort = embeddingPort;
    }

    public Document execute(String title, String rawText, Map<String, Object> metadata) {
        log.info("Executing IngestDocumentUseCase for title: {}", title);

        Document document = new Document(null, title, rawText, metadata);
        documentRepository.save(document);

        List<String> textChunks = splitIntoChunks(rawText, CHUNK_SIZE, CHUNK_OVERLAP);
        List<double[]> embeddings = embeddingPort.generateEmbeddings(textChunks);

        List<DocumentChunk> domainChunks = new ArrayList<>();
        for (int i = 0; i < textChunks.size(); i++) {
            DocumentChunk chunk = new DocumentChunk(
                null,
                document.getId(),
                textChunks.get(i),
                embeddings.get(i),
                i
            );
            domainChunks.add(chunk);
        }

        chunkRepository.saveAll(domainChunks);
        log.info("Saved {} chunks successfully for document ID: {}", domainChunks.size(), document.getId());
        return document;
    }

    private List<String> splitIntoChunks(String text, int size, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        String[] words = text.split("\\s+");
        if (words.length <= size) {
            chunks.add(text);
            return chunks;
        }

        int step = size - overlap;
        if (step <= 0) step = size;

        for (int i = 0; i < words.length; i += step) {
            int end = Math.min(i + size, words.length);
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < end; j++) {
                sb.append(words[j]).append(" ");
            }
            chunks.add(sb.toString().trim());
            if (end == words.length) {
                break;
            }
        }
        return chunks;
    }
}
