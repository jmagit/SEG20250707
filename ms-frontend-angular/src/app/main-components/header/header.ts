import { Component, OnDestroy, signal } from '@angular/core';
import { Login } from "../../security/login/login";
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService, LOGIN_EVENT, LOGOUT_EVENT } from 'src/app/security';
import { environment } from 'src/environments/environment';
import { EventBusService } from 'src/app/common-services';
import { Subscription } from 'rxjs';

export interface Option {
  texto: string
  icono: string
  path?: string
  children?: Child[]
  visible: boolean
}
export interface Child {
  texto: string
  icono: string
  path: string
  separado?: boolean
  visible: boolean
}

@Component({
  selector: 'app-header',
  imports: [Login, RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header implements OnDestroy {
  private login$: Subscription;

  menu = signal<Option[]>([])

  readonly roleMantenimiento = environment.roleMantenimiento
  constructor(public auth: AuthService, private eventBus: EventBusService) {
    this.login$ = this.eventBus.on(LOGIN_EVENT, () => {
      this.actualizaMenu()
    })
    this.login$.add(this.eventBus.on(LOGOUT_EVENT, () => {
      this.actualizaMenu()
    }))
    this.actualizaMenu()
  }
  ngOnDestroy(): void {
    this.login$.unsubscribe();
  }
  actualizaMenu() {
    this.menu.set([
      { texto: 'Inicio', icono: 'fa-solid fa-house', path: '/inicio', visible: true },
      { texto: 'Catalogo', icono: 'fa-solid fa-film', path: '/catalogo', visible: true },
      { texto: 'Categorias', icono: 'fa-solid fa-list', path: this.auth.isInRoles(this.roleMantenimiento) ? '/categorias' : '/catalogo/categorias', visible: true },
      { texto: 'Actores', icono: 'fa-solid fa-person-rays', path: '/actores', visible: true },
      { texto: 'Idiomas', icono: 'fa-solid fa-language', path: '/idiomas', visible: this.auth.isAuthenticated },
    ])
  }
}
