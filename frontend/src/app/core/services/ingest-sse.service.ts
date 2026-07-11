import { Injectable, signal, NgZone, inject } from '@angular/core';
import { IngestionStatusEvent } from '../models/search.model';

@Injectable({
  providedIn: 'root'
})
export class IngestSseService {
  private zone = inject(NgZone);
  private sseUrl = 'http://api.docai.local/api/ingestion/stream';

  private statusState = signal<IngestionStatusEvent[]>([]);
  readonly activeIngestions = this.statusState.asReadonly();

  private eventSource!: EventSource;

  connect(): void {
    this.eventSource = new EventSource(this.sseUrl);

    this.eventSource.onmessage = (event: MessageEvent) => {
      this.zone.run(() => {
        const pipelineUpdate: IngestionStatusEvent = JSON.parse(event.data);

        this.statusState.update(currentList => {
          const index = currentList.findIndex(x => x.documentId === pipelineUpdate.documentId);
          if (index !== -1) {
            const updated = [...currentList];
            updated[index] = pipelineUpdate;
            return updated;
          }
          return [pipelineUpdate, ...currentList];
        });
      });
    };

    this.eventSource.onerror = (err) => {
      console.error('Ingestion SSE Connection lost, attempting reconnection...', err);
    };
  }

  disconnect(): void {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }
}
