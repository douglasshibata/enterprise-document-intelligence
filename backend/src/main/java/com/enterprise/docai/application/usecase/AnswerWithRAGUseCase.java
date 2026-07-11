package com.enterprise.docai.application.usecase;

import com.enterprise.docai.domain.model.RAGContext;
import com.enterprise.docai.domain.model.ScoredDocumentChunk;
import com.enterprise.docai.domain.model.SearchQuery;
import com.enterprise.docai.domain.port.AIAgentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class AnswerWithRAGUseCase {
    private static final Logger log = LoggerFactory.getLogger(AnswerWithRAGUseCase.class);

    private final SearchDocumentsUseCase searchDocumentsUseCase;
    private final AIAgentPort aiAgentPort;

    public AnswerWithRAGUseCase(SearchDocumentsUseCase searchDocumentsUseCase, AIAgentPort aiAgentPort) {
        this.searchDocumentsUseCase = searchDocumentsUseCase;
        this.aiAgentPort = aiAgentPort;
    }

    public RAGContext execute(String question, int maxContextChunks) {
        log.info("Executing AnswerWithRAGUseCase for question: '{}'", question);

        SearchQuery query = new SearchQuery(question, Map.of(), maxContextChunks);
        List<ScoredDocumentChunk> chunks = searchDocumentsUseCase.execute(query).results();

        if (chunks.isEmpty()) {
            log.warn("No relevant document context found for question.");
            return new RAGContext(question, List.of(), "I could not find any relevant information in the uploaded documentation to answer your question.");
        }

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are a professional Enterprise Document Analyst.\n");
        promptBuilder.append("Answer the user's question clearly and accurately, relying strictly on the following document excerpts:\n\n");

        for (int i = 0; i < chunks.size(); i++) {
            ScoredDocumentChunk chunk = chunks.get(i);
            promptBuilder.append(String.format("Excerpt %d (Doc: %s):\n\"\"\"\n%s\n\"\"\"\n\n",
                i + 1, chunk.documentTitle(), chunk.text()));
        }

        promptBuilder.append("If the excerpts do not contain enough information to answer, state that clearly.\n\n");
        promptBuilder.append(String.format("User Question: \"%s\"\n\nAnswer: ", question));

        String answer = aiAgentPort.generateAnswer(promptBuilder.toString());

        log.info("RAG generation finished. Question: '{}', Excerpts: {}, Output length: {}",
            question, chunks.size(), answer.length());

        return new RAGContext(question, chunks, answer);
    }
}
