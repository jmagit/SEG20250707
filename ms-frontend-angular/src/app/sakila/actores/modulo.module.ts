import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ACTORES_COMPONENTES, ActoresAdd, ActoresEdit, ActoresList, ActoresView } from './componente';
import { environment } from 'src/environments/environment';
import { AuthWithRedirectCanActivate, InRoleCanActivate } from '../../security';

export const routes: Routes = [
  { path: '', component: ActoresList },
  { path: 'add', component: ActoresAdd,
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)] },
  { path: ':id/edit', component: ActoresEdit,
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)] },
  { path: ':id', component: ActoresView },
  { path: ':id/:kk', component: ActoresView },
];

@NgModule({
  declarations: [],
  exports: [
    RouterModule
  ],
  imports: [
    RouterModule.forChild(routes), ACTORES_COMPONENTES,
  ]
})
export default class ActoresModule { }
