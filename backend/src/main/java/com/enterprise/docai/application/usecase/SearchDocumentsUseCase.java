package com.enterprise.docai.application.usecase;

import com.enterprise.docai.domain.model.ScoredDocumentChunk;
import com.enterprise.docai.domain.model.SearchQuery;
import com.enterprise.docai.domain.model.SearchResult;
import com.enterprise.docai.domain.port.ChunkRepositoryPort;
import com.enterprise.docai.domain.port.DocumentEmbeddingPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SearchDocumentsUseCase {
    private static final Logger log = LoggerFactory.getLogger(SearchDocumentsUseCase.class);

    private final ChunkRepositoryPort chunkRepository;
    private final DocumentEmbeddingPort embeddingPort;

    public SearchDocumentsUseCase(ChunkRepositoryPort chunkRepository, DocumentEmbeddingPort embeddingPort) {
        this.chunkRepository = chunkRepository;
        this.embeddingPort = embeddingPort;
    }

    public SearchResult execute(SearchQuery query) {
        log.info("Executing SearchDocumentsUseCase with text: '{}'", query.queryText());

        double[] queryEmbedding = embeddingPort.generateEmbedding(query.queryText());
        List<ScoredDocumentChunk> topChunks = chunkRepository.findTopKSimilar(queryEmbedding, query.limit());

        return new SearchResult(query.queryText(), topChunks);
    }
}
