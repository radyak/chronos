import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityNetworkGraphComponent } from './entity-network-graph.component';

describe('EntityNetworkGraphComponent', () => {
  let component: EntityNetworkGraphComponent;
  let fixture: ComponentFixture<EntityNetworkGraphComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntityNetworkGraphComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityNetworkGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
