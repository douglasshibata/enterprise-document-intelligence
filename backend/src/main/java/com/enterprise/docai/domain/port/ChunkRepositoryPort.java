package com.enterprise.docai.domain.port;

import com.enterprise.docai.domain.model.DocumentChunk;
import com.enterprise.docai.domain.model.ScoredDocumentChunk;
import java.util.List;

public interface ChunkRepositoryPort {
    void saveAll(List<DocumentChunk> chunks);
    List<ScoredDocumentChunk> findTopKSimilar(double[] queryEmbedding, int k);
}
