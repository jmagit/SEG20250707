/* eslint-disable @typescript-eslint/no-explicit-any */

import { CommonModule } from '@angular/common';
import { Injectable, Component, OnChanges, OnDestroy, Input, SimpleChanges, OnInit, effect, input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ErrorMessagePipe, NormalizePipe, NotblankValidator, UppercaseValidator, TypeValidator } from '@my/library';
import { Paginator } from '../../common-components';

import { ViewModelPagedService } from '../../core';
import { FormButtons } from '../../common-components';
import { PeliculasListBody } from '../peliculas';
import { environment } from 'src/environments/environment';
import { ActoresDAOService } from '../daos-services';
import { CommonComponentsModule } from "../../common-components/common-components.module";
import { ListButtons } from "../../common-components/list-buttons";

@Injectable({
  providedIn: 'root'
})
export class ActoresViewModelService extends ViewModelPagedService<any, number> {
  constructor(dao: ActoresDAOService) {
    super(dao)
  }

  peliculas: any[] = []

  public override afterView(): void {
    (this.dao as ActoresDAOService).peliculas(this.elemento.id).subscribe({
      next: data => {
        this.peliculas = data //.map(item => ({filmId: item.key, title: item.value }));
      },
      error: err => this.handleError(err)
    });
  }
}

@Component({
  selector: 'app-actores-list',
  templateUrl: './tmpl-list.html',
  styleUrls: ['./componente.css'],
  imports: [RouterLink, Paginator, CommonModule, NormalizePipe, CommonComponentsModule, ListButtons]
})
export class ActoresList implements OnDestroy {
  readonly roleMantenimiento = environment.roleMantenimiento
  readonly page = input(0);

  constructor(protected vm: ActoresViewModelService) {
    effect(() => vm.load(this.page()))
  }

  public get VM(): ActoresViewModelService { return this.vm; }

  ngOnDestroy(): void { this.vm.clear(); }
}

@Component({
  selector: 'app-actores-add',
  templateUrl: './tmpl-form.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, ErrorMessagePipe, NotblankValidator, UppercaseValidator, TypeValidator, FormButtons]
})
export class ActoresAdd implements OnInit {
  constructor(protected vm: ActoresViewModelService) { }
  public get VM(): ActoresViewModelService { return this.vm; }
  ngOnInit(): void {
    this.vm.add();
  }
}

@Component({
  selector: 'app-actores-edit',
  templateUrl: './tmpl-form.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [FormsModule, CommonModule, ErrorMessagePipe, NotblankValidator, UppercaseValidator, TypeValidator, FormButtons]
})
export class ActoresEdit implements OnChanges {
  @Input() id?: string;
  constructor(protected vm: ActoresViewModelService, protected router: Router) { }
  public get VM(): ActoresViewModelService { return this.vm; }
  ngOnChanges(_changes: SimpleChanges): void {
    if (this.id) {
      this.vm.edit(+this.id);
    } else {
      this.router.navigate(['/404.html']);
    }
  }
}

@Component({
  selector: 'app-actores-view',
  templateUrl: './tmpl-view.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [ FormButtons, PeliculasListBody, ]
})
export class ActoresView {
  constructor(protected vm: ActoresViewModelService, protected router: Router) { }
  public get VM(): ActoresViewModelService { return this.vm; }
  @Input() set id(key: string) {
    if (+key) {
      this.vm.view(+key);
    } else {
      this.router.navigate(['/404.html']);
    }
  }
}


export const ACTORES_COMPONENTES = [ActoresList, ActoresAdd, ActoresEdit, ActoresView,];
