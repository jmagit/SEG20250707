import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormButtons as FormButtons } from './form-buttons';

describe('FormButtons', () => {
  let component: FormButtons;
  let fixture: ComponentFixture<FormButtons>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [FormButtons]
})
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormButtons);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
