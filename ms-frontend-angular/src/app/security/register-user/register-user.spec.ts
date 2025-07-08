import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterUser } from './register-user';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { LoggerService } from '@my/library';
import { provideHttpClient } from '@angular/common/http';

describe('RegisterUser', () => {
  let component: RegisterUser;
  let fixture: ComponentFixture<RegisterUser>;

  beforeEach(async() => {
    const routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);
    TestBed.configureTestingModule({
        schemas: [NO_ERRORS_SCHEMA],
        providers: [LoggerService, provideHttpClient(), provideHttpClientTesting(),
            { provide: Router, useValue: routerSpy }],
        imports: [RegisterUser,],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterUser);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
