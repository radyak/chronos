import { TestBed } from '@angular/core/testing';

import { HistoricalDataClient } from './historical-data.client';

describe('HistoricalDataClient', () => {
  let service: HistoricalDataClient;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HistoricalDataClient);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
