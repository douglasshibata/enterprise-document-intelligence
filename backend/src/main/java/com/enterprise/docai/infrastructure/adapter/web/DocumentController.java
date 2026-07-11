package com.enterprise.docai.infrastructure.adapter.web;

import com.enterprise.docai.application.usecase.IngestDocumentUseCase;
import com.enterprise.docai.application.usecase.SearchDocumentsUseCase;
import com.enterprise.docai.application.usecase.AnswerWithRAGUseCase;
import com.enterprise.docai.domain.model.Document;
import com.enterprise.docai.domain.model.SearchQuery;
import com.enterprise.docai.domain.model.SearchResult;
import com.enterprise.docai.domain.model.RAGContext;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final IngestDocumentUseCase ingestDocumentUseCase;
    private final SearchDocumentsUseCase searchDocumentsUseCase;
    private final AnswerWithRAGUseCase answerWithRAGUseCase;

    public DocumentController(IngestDocumentUseCase ingestDocumentUseCase,
                              SearchDocumentsUseCase searchDocumentsUseCase,
                              AnswerWithRAGUseCase answerWithRAGUseCase) {
        this.ingestDocumentUseCase = ingestDocumentUseCase;
        this.searchDocumentsUseCase = searchDocumentsUseCase;
        this.answerWithRAGUseCase = answerWithRAGUseCase;
    }

    public record IngestRequest(
        @NotEmpty(message = "Title is mandatory") String title,
        @NotEmpty(message = "Content is mandatory") String content,
        Map<String, Object> metadata
    ) {}

    public record RAGRequest(
        @NotEmpty(message = "Question is mandatory") String question,
        int limit
    ) {}

    @PostMapping("/documents")
    public ResponseEntity<Document> ingestDocument(@RequestBody IngestRequest request) {
        Document document = ingestDocumentUseCase.execute(
            request.title(),
            request.content(),
            request.metadata() != null ? request.metadata() : Map.of()
        );
        return ResponseEntity.ok(document);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResult> searchDocuments(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        SearchQuery searchQuery = new SearchQuery(query, Map.of(), limit);
        SearchResult result = searchDocumentsUseCase.execute(searchQuery);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/rag/ask")
    public ResponseEntity<RAGContext> askWithRAG(@RequestBody RAGRequest request) {
        int maxChunks = request.limit() > 0 ? request.limit() : 5;
        RAGContext response = answerWithRAGUseCase.execute(request.question(), maxChunks);
        return ResponseEntity.ok(response);
    }
}
