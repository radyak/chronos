import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRelationOffcanvasComponent } from './edit-relation-offcanvas.component';

describe('EditRelationOffcanvasComponent', () => {
  let component: EditRelationOffcanvasComponent;
  let fixture: ComponentFixture<EditRelationOffcanvasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditRelationOffcanvasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditRelationOffcanvasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
