import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { NonNullableFormBuilder, Validators } from '@angular/forms';
import { SearchService } from '../../core/services/search.service';
import { IngestSseService } from '../../core/services/ingest-sse.service';

@Component({
  selector: 'app-ingest-manager',
  standalone: false,
  template: `
    <div class="max-w-4xl mx-auto p-6 grid grid-cols-1 md:grid-cols-2 gap-8">

      <!-- Upload / Custom Text ingestion panel -->
      <div class="bg-white p-6 rounded-xl border border-gray-100 shadow-sm space-y-4">
        <h2 class="text-xl font-bold text-gray-900">Document Ingest Panel</h2>
        <form [formGroup]="ingestForm" (ngSubmit)="submitDocument()" class="space-y-4">
          <div class="space-y-1">
            <label class="block text-sm font-medium text-gray-700">Document Title</label>
            <input
              type="text"
              formControlName="title"
              placeholder="e.g. Agile_Best_Practices.pdf"
              class="w-full p-2.5 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div class="space-y-1">
            <label class="block text-sm font-medium text-gray-700">Document Content</label>
            <textarea
              formControlName="content"
              placeholder="Paste raw text or wait for background folder watcher processing..."
              class="w-full h-44 p-2.5 border border-gray-200 rounded-lg outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
            ></textarea>
          </div>

          <button
            type="submit"
            [disabled]="ingestForm.invalid"
            class="w-full py-3 bg-indigo-600 hover:bg-indigo-700 text-white font-medium rounded-lg transition duration-150 cursor-pointer disabled:opacity-50"
          >
            Trigger Ingestion Use Case
          </button>
        </form>
      </div>

      <!-- Live background pipeline ingestion monitor -->
      <div class="bg-white p-6 rounded-xl border border-gray-100 shadow-sm space-y-4">
        <div class="flex justify-between items-center border-b pb-3">
          <h2 class="text-xl font-bold text-gray-900">Background Indexing</h2>
          <span class="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
            <span class="w-1.5 h-1.5 mr-1 bg-green-500 rounded-full animate-pulse"></span>
            SSE Channel
          </span>
        </div>

        <div class="space-y-4 overflow-y-auto max-h-[20rem]">
          @for (task of activeTasks(); track task.documentId) {
            <div class="p-3 border rounded-lg space-y-2">
              <div class="flex justify-between items-center text-sm">
                <span class="font-semibold text-gray-800 truncate max-w-[12rem]">{{ task.title }}</span>
                <span class="text-xs font-medium px-2 py-0.5 rounded" [ngClass]="{
                  'bg-yellow-100 text-yellow-800': task.status !== 'COMPLETED' && task.status !== 'FAILED',
                  'bg-green-100 text-green-800': task.status === 'COMPLETED',
                  'bg-red-100 text-red-800': task.status === 'FAILED'
                }">{{ task.status }}</span>
              </div>
              <div class="w-full bg-gray-100 rounded-full h-2">
                <div
                  class="bg-indigo-600 h-2 rounded-full transition-all duration-300"
                  [style.width.%]="task.progressPercentage"
                ></div>
              </div>
            </div>
          } @empty {
            <p class="text-center text-sm text-gray-500 py-10">No active background ingestion tasks in progress.</p>
          }
        </div>
      </div>
    </div>
  `
})
export class IngestManagerComponent implements OnInit, OnDestroy {
  private fb = inject(NonNullableFormBuilder);
  private searchService = inject(SearchService);
  private ingestSseService = inject(IngestSseService);

  readonly activeTasks = this.ingestSseService.activeIngestions;

  ingestForm = this.fb.group({
    title: ['', [Validators.required]],
    content: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.ingestSseService.connect();
  }

  ngOnDestroy(): void {
    this.ingestSseService.disconnect();
  }

  submitDocument(): void {
    if (this.ingestForm.invalid) return;

    this.searchService.ingestDocument(this.ingestForm.getRawValue()).subscribe({
      next: () => {
        this.ingestForm.reset();
      },
      error: (err) => {
        console.error('API Manual Ingestion Failed', err);
      }
    });
  }
}