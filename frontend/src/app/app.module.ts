import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { SearchDashboardComponent } from './features/search-dashboard/search-dashboard.component';
import { IngestManagerComponent } from './features/ingest-manager/ingest-manager.component';

@NgModule({
  declarations: [
    SearchDashboardComponent,
    IngestManagerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    CommonModule
  ],
  providers: [],
  bootstrap: []
})
export class AppDocIntelligenceModule {}
