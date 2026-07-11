export interface Document {
  id: string;
  title: string;
  content: string;
  metadata: Record<string, any>;
}

export interface ScoredDocumentChunk {
  chunkId: string;
  documentId: string;
  documentTitle: string;
  text: string;
  score: number;
}

export interface SearchResult {
  queryText: string;
  results: ScoredDocumentChunk[];
}

export interface IngestRequest {
  title: string;
  content: string;
  metadata?: Record<string, any>;
}

export interface IngestionStatusEvent {
  documentId: string;
  title: string;
  status: 'PENDING' | 'EXTRACTING' | 'CHUNKING' | 'EMBEDDING' | 'COMPLETED' | 'FAILED';
  progressPercentage: number;
  timestamp: string;
}
