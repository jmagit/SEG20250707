import { Injectable, Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ErrorMessagePipe, NotblankValidator } from '@my/library';
import { ViewModelService } from '../../core';
import { FormsModule } from '@angular/forms';
import { FormButtons } from '../../common-components';
import { CategoriasDAOService } from '../daos-services';
import { NotificationType, WindowService } from 'src/app/common-services';


@Injectable({
  providedIn: 'root'
})
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export class CategoriasViewModelService extends ViewModelService<any, number> {
  constructor(dao: CategoriasDAOService, private window: WindowService) {
    super(dao)
  }
  public override cancel(): void {
      this.clear()
      this.notify.clear()
      this.list()
  }

  public override delete(key: number, nextFn?: (hook?: boolean) => void): void {
    this.window.confirm('Una vez borrado no se podrá recuperar. ¿Continuo?',
      () => super.delete(key, nextFn), NotificationType.error, "Confirmación")
  }
}

@Component({
  selector: 'app-categorias',
  templateUrl: './tmpl-anfitrion.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [ FormsModule, RouterLink, FormButtons, ErrorMessagePipe, NotblankValidator, ]
})
export class Categorias implements OnInit {
  constructor(protected vm: CategoriasViewModelService) { }
  public get VM(): CategoriasViewModelService { return this.vm; }
  ngOnInit(): void {
    this.vm.list();
  }
}

export const CATEGORIAS_COMPONENTES = [ Categorias, ];
