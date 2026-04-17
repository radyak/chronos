import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEntityRelationFormComponent } from './edit-entity-relation-form.component';

describe('EditEntityRelationFormComponent', () => {
  let component: EditEntityRelationFormComponent;
  let fixture: ComponentFixture<EditEntityRelationFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditEntityRelationFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditEntityRelationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
