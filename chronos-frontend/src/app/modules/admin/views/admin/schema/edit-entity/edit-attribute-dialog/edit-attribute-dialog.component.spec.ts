import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAttributeDialogComponent } from './edit-attribute-dialog.component';

describe('EditEntityAttributeDialogComponent', () => {
  let component: EditAttributeDialogComponent;
  let fixture: ComponentFixture<EditAttributeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditAttributeDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditAttributeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
