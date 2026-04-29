import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntitySelectComponent } from './entity-select.component';

describe('EntitySelectComponent', () => {
  let component: EntitySelectComponent;
  let fixture: ComponentFixture<EntitySelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntitySelectComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntitySelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
