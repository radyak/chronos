import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEntityAttributeDialogComponent } from './edit-entity-attribute-dialog.component';

describe('EditEntityAttributeDialogComponent', () => {
  let component: EditEntityAttributeDialogComponent;
  let fixture: ComponentFixture<EditEntityAttributeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditEntityAttributeDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditEntityAttributeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
