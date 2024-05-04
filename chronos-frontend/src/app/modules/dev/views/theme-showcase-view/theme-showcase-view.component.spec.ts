import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ThemeShowcaseViewComponent} from './theme-showcase-view.component';

describe('ThemeShowcaseViewComponent', () => {
  let component: ThemeShowcaseViewComponent;
  let fixture: ComponentFixture<ThemeShowcaseViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThemeShowcaseViewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ThemeShowcaseViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
