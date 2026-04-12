import { TestBed } from '@angular/core/testing';

import { SchemaClient } from './schema.client';

describe('SchemaClient', () => {
  let client: SchemaClient;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    client = TestBed.inject(SchemaClient);
  });

  it('should be created', () => {
    expect(client).toBeTruthy();
  });
});
