import { Component, inject } from '@angular/core';
import { SearchService } from '../../core/services/search.service';

@Component({
  selector: 'app-search-dashboard',
  standalone: false,
  template: `
    <div class="max-w-4xl mx-auto p-6 space-y-6">
      <div class="text-center space-y-2">
        <h1 class="text-3xl font-extrabold text-gray-900 tracking-tight">Enterprise Semantic Explorer</h1>
        <p class="text-gray-500">Query your corporate documents semantically using advanced vector embeddings</p>
      </div>

      <!-- Search Input Container -->
      <div class="flex items-center space-x-3 bg-white p-2 rounded-xl shadow-md border border-gray-100">
        <div class="pl-3 text-gray-400">
          <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
          </svg>
        </div>
        <input
          #queryInput
          type="text"
          placeholder="e.g. agile project planning best practices or contract indemnity clauses..."
          class="flex-1 p-3 text-base text-gray-800 outline-none placeholder-gray-400"
          (keyup.enter)="triggerSearch(queryInput.value)"
        />
        <button
          [disabled]="searching()"
          (click)="triggerSearch(queryInput.value)"
          class="px-6 py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg shadow transition duration-150 cursor-pointer disabled:opacity-50"
        >
          {{ searching() ? 'Searching...' : 'Search' }}
        </button>
      </div>

      <!-- Search Results Area -->
      <div class="space-y-4">
        @if (results()) {
          <div class="flex justify-between items-center px-1">
            <p class="text-sm text-gray-500">Found {{ results()?.results?.length || 0 }} semantic context matches</p>
          </div>

          @for (chunk of results()?.results; track chunk.chunkId) {
            <div class="bg-white p-5 rounded-xl border border-gray-150 hover:shadow-md transition duration-150 space-y-3">
              <div class="flex justify-between items-start">
                <div>
                  <h3 class="font-bold text-gray-800 text-lg">{{ chunk.documentTitle }}</h3>
                  <p class="text-xs text-gray-400">Chunk Association ID: {{ chunk.chunkId }}</p>
                </div>
                <span class="px-2.5 py-1 text-xs font-semibold rounded-full bg-indigo-100 text-indigo-800 flex items-center">
                  Score: {{ (chunk.score * 100) | number:'1.1-1' }}%
                </span>
              </div>
              <p class="text-gray-700 text-base leading-relaxed pl-2 border-l-4 border-indigo-400">
                {{ chunk.text }}
              </p>
            </div>
          } @empty {
            <div class="text-center py-12 bg-white rounded-xl border border-gray-100">
              <p class="text-gray-500">No matches found for "{{ results()?.queryText }}". Try broadening your terms.</p>
            </div>
          }
        }
      </div>
    </div>
  `
})
export class SearchDashboardComponent {
  private searchService = inject(SearchService);

  readonly searching = this.searchService.isSearching;
  readonly results = this.searchService.searchResult;

  triggerSearch(query: string): void {
    if (!query || !query.trim()) return;
    this.searchService.search(query).subscribe();
  }
}
