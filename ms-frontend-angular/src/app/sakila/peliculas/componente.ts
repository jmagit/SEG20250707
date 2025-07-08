import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PeliculasViewModelService } from './servicios-service';

import { ErrorMessagePipe, ExecPipe, NormalizePipe, NotblankValidator, TypeValidator } from '@my/library';
import { CommonModule } from '@angular/common';
import { Paginator } from '../../common-components';

import { FormsModule } from '@angular/forms';
import { FormButtons } from '../../common-components';

@Component({
  selector: 'app-peliculas-list-body',
  templateUrl: './tmpl-list-body.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [RouterLink, NormalizePipe, CommonModule, ]
})
export class PeliculasListBody {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  @Input({required: true}) Listado: any[] = []
  @Input() urlBase = ''
  @Output() imageError = new EventEmitter<Event>()

  imageErrorHandler(event: Event) {
    this.imageError.emit(event)
  }
}

@Component({
  selector: 'app-peliculas-list',
  templateUrl: './tmpl-list.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [RouterLink, Paginator, PeliculasListBody, FormsModule, ErrorMessagePipe ],
})
export class PeliculasList implements OnChanges, OnDestroy {
  @Input() page = 0
  @Input() search = ''
  @Input() categoria? : number
  pagina = false
  constructor(protected vm: PeliculasViewModelService) { }

  public get VM(): PeliculasViewModelService { return this.vm; }

  ngOnChanges(_changes: SimpleChanges): void {
    // if (this.type == 'categorias') {
    //   this.vm.list()
    if (this.categoria) {
      this.vm.porCategorias(this.categoria)
    } else if (this.search == 'categorias') {
      this.vm.cargaCategorias()
    } else{
      this.pagina = true;
      this.vm.load(this.page)
    }
  }

  ngOnDestroy(): void { this.vm.clear(); }
}

@Component({
  selector: 'app-peliculas-add',
  templateUrl: './tmpl-form.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, ErrorMessagePipe, NotblankValidator, TypeValidator, FormButtons, ExecPipe, ]
})
export class PeliculasAdd implements OnInit {
  constructor(protected vm: PeliculasViewModelService) { }
  public get VM(): PeliculasViewModelService { return this.vm; }
  ngOnInit(): void {
    this.vm.add();
  }
}

@Component({
  selector: 'app-peliculas-edit',
  templateUrl: './tmpl-form.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, ErrorMessagePipe, NotblankValidator, TypeValidator, FormButtons, ExecPipe, ]
})
export class PeliculasEdit implements OnChanges {
  @Input() id?: string;
  constructor(protected vm: PeliculasViewModelService, protected router: Router) { }
  public get VM(): PeliculasViewModelService { return this.vm; }
  ngOnChanges(_changes: SimpleChanges): void {
    if (this.id) {
      this.vm.edit(+this.id);
    } else {
      this.router.navigate(['/404.html']);
    }
  }
}

@Component({
  selector: 'app-peliculas-view',
  templateUrl: './tmpl-view.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [RouterLink, CommonModule, FormButtons, ]
})
export class PeliculasView {
  constructor(protected vm: PeliculasViewModelService, protected router: Router) { }
  public get VM(): PeliculasViewModelService { return this.vm; }
  @Input() set id(key: string) {
    if (+key) {
      this.vm.view(+key);
    } else {
      this.router.navigate(['/404.html']);
    }
  }
}

@Component({
  selector: 'app-peliculas',
  templateUrl: './tmpl-anfitrion.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [PeliculasList, PeliculasAdd, PeliculasEdit, PeliculasView, ],
})
export class Peliculas implements OnInit, OnDestroy {
  constructor(protected vm: PeliculasViewModelService, private route: ActivatedRoute) { }
  public get VM(): PeliculasViewModelService { return this.vm; }
  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      if (this.route.snapshot.url.slice(-1)[0]?.path === 'edit') {
        this.vm.edit(+id);
      } else {
        this.vm.view(+id);
      }
    } else if (this.route.snapshot.url.slice(-1)[0]?.path === 'add') {
      this.vm.add();
    } else {
      this.vm.load(this.route.snapshot.queryParams['page'] ?? 0);
    }
  }
  ngOnDestroy(): void { this.vm.clear(); }
}

export const PELICULAS_COMPONENTES = [
  Peliculas, PeliculasList, PeliculasAdd,
  PeliculasEdit, PeliculasView, PeliculasListBody,
];
