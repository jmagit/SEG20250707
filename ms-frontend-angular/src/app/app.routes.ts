import { Routes, UrlSegment } from '@angular/router';
import { PageNotFound } from './main-components';
import { AuthWithRedirectCanActivate, InRoleCanActivate, LoginForm, RegisterUser } from './security';
import { PeliculasList, peliculasRoutes, actoresRoutes, Novedades } from './sakila';
import { environment } from 'src/environments/environment';

export function svgFiles(url: UrlSegment[]) {
  return url.length === 1 && url[0].path.endsWith('.svg') ? ({consumed: url}) : null;
}

export const routes: Routes = [
  { path: '', pathMatch: 'full', component: Novedades},
  { path: 'inicio', component: Novedades},
  { path: 'catalogo/categorias', pathMatch: 'full', component: PeliculasList, data: { search: 'categorias' }, title: 'categorias' },
  { path: 'catalogo/categorias/:idPeli/:tit', redirectTo: '/catalogo/:idPeli/:tit', title: 'catalogo' },
  { path: 'catalogo', children: peliculasRoutes, title: 'catalogo' },
  { path: 'actores/:id/:nom/:idPeli/:tit', redirectTo: '/catalogo/:idPeli/:tit', title: 'catalogo' },
  {
    path: 'actores', children: actoresRoutes, title: 'actores'
  },
  {
    path: 'categorias', loadChildren: () => import('./sakila/categorias/modulo.module'), title: 'categorias',
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)]
  },
  {
    path: 'idiomas', loadChildren: () => import('./sakila/idiomas/modulo.module'), title: 'idiomas',
    canActivate: [AuthWithRedirectCanActivate('/login'), InRoleCanActivate(environment.roleMantenimiento)]
  },

  { path: 'login', component: LoginForm },
  { path: 'registro', component: RegisterUser },

  { path: '404.html', component: PageNotFound },
  { path: '**', component: PageNotFound /*, redirectTo: '/inicio'*/ },
];
