import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WikiArticleInputComponent } from './wiki-article-input.component';

describe('WikiArticleSearchComponent', () => {
  let component: WikiArticleInputComponent;
  let fixture: ComponentFixture<WikiArticleInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WikiArticleInputComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WikiArticleInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
