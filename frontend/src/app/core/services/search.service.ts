import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { SearchResult, Document, IngestRequest } from '../models/search.model';

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private http = inject(HttpClient);
  private apiUrl = 'http://api.docai.local/api';

  private searchState = signal<SearchResult | null>(null);
  readonly searchResult = this.searchState.asReadonly();

  readonly isSearching = signal<boolean>(false);

  search(query: string, limit: number = 10): Observable<SearchResult> {
    this.isSearching.set(true);
    const url = `${this.apiUrl}/search?q=${encodeURIComponent(query)}&limit=${limit}`;
    return this.http.get<SearchResult>(url).pipe(
      tap({
        next: (result) => {
          this.searchState.set(result);
          this.isSearching.set(false);
        },
        error: () => {
          this.isSearching.set(false);
        }
      })
    );
  }

  ingestDocument(request: IngestRequest): Observable<Document> {
    const url = `${this.apiUrl}/documents`;
    return this.http.post<Document>(url, request);
  }
}
