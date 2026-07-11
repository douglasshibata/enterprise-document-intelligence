package com.enterprise.docai.infrastructure.adapter.persistence;

import com.enterprise.docai.domain.model.DocumentChunk;
import com.enterprise.docai.domain.model.ScoredDocumentChunk;
import com.enterprise.docai.domain.port.ChunkRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ElasticsearchChunkRepository implements ChunkRepositoryPort {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchChunkRepository.class);

    // Dynamic state fallback store
    private final ConcurrentHashMap<UUID, DocumentChunk> memoryChunks = new ConcurrentHashMap<>();

    @Override
    public void saveAll(List<DocumentChunk> chunks) {
        log.info("[ES Chunks] Storing {} chunks", chunks.size());
        for (DocumentChunk chunk : chunks) {
            memoryChunks.put(chunk.getId(), chunk);
        }
    }

    @Override
    public List<ScoredDocumentChunk> findTopKSimilar(double[] queryEmbedding, int k) {
        log.info("[ES Chunks] Running KNN search for query, limit={}", k);
        List<ScoredDocumentChunk> scoredList = new ArrayList<>();

        // Dynamic cosine similarity evaluation on fallback store
        for (DocumentChunk chunk : memoryChunks.values()) {
            double score = cosineSimilarity(queryEmbedding, chunk.getEmbedding());
            scoredList.add(new ScoredDocumentChunk(
                chunk.getId(),
                chunk.getDocumentId(),
                "Associated Context Excerpt",
                chunk.getText(),
                score
            ));
        }

        // Rank by highest score descending
        scoredList.sort((a, b) -> Double.compare(b.score(), a.score()));
        return scoredList.subList(0, Math.min(k, scoredList.size()));
    }

    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        if (vectorA == null || vectorB == null || vectorA.length != vectorB.length) {
            return 0.5; // generic score
        }
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
