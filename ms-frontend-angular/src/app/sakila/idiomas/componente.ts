import { Injectable, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ErrorMessagePipe, NotblankValidator } from '@my/library';
import { ViewModelService } from '../../core';
import { FormButtons } from '../../common-components';
import { IdiomasDAOService } from '../daos-services';
import { NotificationType, WindowService } from 'src/app/common-services';

@Injectable({
  providedIn: 'root'
})
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export class IdiomasViewModelService extends ViewModelService<any, number> {
  constructor(dao: IdiomasDAOService, private window: WindowService) {
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
  selector: 'app-idiomas',
  templateUrl: './tmpl-anfitrion.html',
  styleUrls: ['./componente.css'],
  standalone: true,
  imports: [FormsModule, FormButtons, ErrorMessagePipe, NotblankValidator,],
})
export class Idiomas implements OnInit {
  constructor(protected vm: IdiomasViewModelService) { }
  public get VM(): IdiomasViewModelService { return this.vm; }
  ngOnInit(): void {
    this.vm.list();
  }
}

export const IDIOMAS_COMPONENTES = [ Idiomas, ];
