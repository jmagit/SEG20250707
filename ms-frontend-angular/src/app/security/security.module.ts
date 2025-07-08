import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Login, LoginForm } from './login/login';
import { RegisterUser } from './register-user/register-user';



@NgModule({
  declarations: [],
  exports: [ Login, LoginForm, RegisterUser, ],
  imports: [
    CommonModule, Login, LoginForm, RegisterUser,
  ]
})
export class SecurityModule { }
