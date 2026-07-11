package com.enterprise.docai.domain.port;

import java.util.List;

public interface DocumentEmbeddingPort {
    double[] generateEmbedding(String text);
    List<double[]> generateEmbeddings(List<String> texts);
}
