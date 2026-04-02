import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEntityComponent } from './edit-entity.component';

describe('EditEntityComponent', () => {
  let component: EditEntityComponent;
  let fixture: ComponentFixture<EditEntityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditEntityComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditEntityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
