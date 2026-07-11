package com.enterprise.docai.infrastructure.adapter.route;

import com.enterprise.docai.application.usecase.IngestDocumentUseCase;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

@Component
public class IngestionRouteBuilder extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(IngestionRouteBuilder.class);

    private final IngestDocumentUseCase ingestDocumentUseCase;

    public IngestionRouteBuilder(IngestDocumentUseCase ingestDocumentUseCase) {
        this.ingestDocumentUseCase = ingestDocumentUseCase;
    }

    @Override
    public void configure() throws Exception {
        // Handle Ingestion Failures via Dead Letter Channel
        errorHandler(deadLetterChannel("kafka:ingestion-failures-dlq?brokers={{camel.route.kafka.brokers}}")
            .maximumRedeliveries(2)
            .redeliveryDelay(1000)
            .log("Error in ingestion route: ${exception.message}"));

        // Route 1: Watch Folder Inbox
        from("file:{{camel.route.file.inbox-dir}}?delete=true&noop=false")
            .routeId("FileInboxIngestionRoute")
            .process(exchange -> {
                File file = exchange.getIn().getBody(File.class);
                log.info("Processing file: {} via Simple Ingestion Route", file.getName());

                String extractedText = Files.readString(file.toPath());
                String title = file.getName();

                ingestDocumentUseCase.execute(title, extractedText, Map.of(
                    "source", "file-inbox",
                    "file_size", file.length()
                ));
            })
            .log("Successfully ingested file: ${header.CamelFileName}");

        // Route 2: Kafka Documents Feed
        from("kafka:{{camel.route.kafka.topic}}?brokers={{camel.route.kafka.brokers}}")
            .routeId("KafkaDocIngestionRoute")
            .process(exchange -> {
                String body = exchange.getIn().getBody(String.class);
                log.info("Received Kafka doc message: {}", body);

                // Fallback / Parse JSON simply
                String title = "Kafka Event Document " + System.currentTimeMillis();
                ingestDocumentUseCase.execute(title, body, Map.of("source", "kafka"));
            })
            .log("Successfully processed Kafka document.");
    }
}
