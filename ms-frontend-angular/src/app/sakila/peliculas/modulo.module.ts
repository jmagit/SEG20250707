import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PELICULAS_COMPONENTES, PeliculasAdd, PeliculasEdit, PeliculasView, PeliculasList } from './componente';
import { AuthWithRedirectCanActivate, InRoleCanActivate } from '../../security';
import { environment } from '../../../environments/environment';

export const routes: Routes = [
  { path: '', component: PeliculasList },
  {
    path: 'add', component: PeliculasAdd,
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)]
  },
  {
    path: ':id/edit', component: PeliculasEdit,
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)]
  },
  { path: ':id', component: PeliculasView },
  { path: ':id/:kk', component: PeliculasView },
];

@NgModule({
  declarations: [ ],
  exports: [
    PELICULAS_COMPONENTES,
    RouterModule,
  ],
  imports: [
    RouterModule.forChild(routes), PELICULAS_COMPONENTES,
  ]
})
export class PeliculasModule { }
