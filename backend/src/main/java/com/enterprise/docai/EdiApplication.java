package com.enterprise.docai;

import com.enterprise.docai.application.usecase.AnswerWithRAGUseCase;
import com.enterprise.docai.application.usecase.IngestDocumentUseCase;
import com.enterprise.docai.application.usecase.SearchDocumentsUseCase;
import com.enterprise.docai.domain.port.AIAgentPort;
import com.enterprise.docai.domain.port.ChunkRepositoryPort;
import com.enterprise.docai.domain.port.DocumentEmbeddingPort;
import com.enterprise.docai.domain.port.DocumentRepositoryPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EdiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdiApplication.class, args);
    }

    @Bean
    public IngestDocumentUseCase ingestDocumentUseCase(
            DocumentRepositoryPort documentRepository,
            ChunkRepositoryPort chunkRepository,
            DocumentEmbeddingPort embeddingPort) {
        return new IngestDocumentUseCase(documentRepository, chunkRepository, embeddingPort);
    }

    @Bean
    public SearchDocumentsUseCase searchDocumentsUseCase(
            ChunkRepositoryPort chunkRepository,
            DocumentEmbeddingPort embeddingPort) {
        return new SearchDocumentsUseCase(chunkRepository, embeddingPort);
    }

    @Bean
    public AnswerWithRAGUseCase answerWithRAGUseCase(
            SearchDocumentsUseCase searchDocumentsUseCase,
            AIAgentPort aiAgentPort) {
        return new AnswerWithRAGUseCase(searchDocumentsUseCase, aiAgentPort);
    }
}
