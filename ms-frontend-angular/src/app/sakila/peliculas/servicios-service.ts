/* eslint-disable @typescript-eslint/no-explicit-any */
import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ViewModelPagedService } from '../../core';
import { PeliculasDAOService, IdiomasDAOService, ActoresDAOService, CategoriasDAOService } from '../daos-services';
import { NotificationType, WindowService } from 'src/app/common-services';

@Injectable({
  providedIn: 'root'
})
export class PeliculasViewModelService extends ViewModelPagedService<any, number> {
  readonly roleMantenimiento = environment.roleMantenimiento

  constructor(dao: PeliculasDAOService, protected daoIdiomas: IdiomasDAOService, protected daoCategorias: CategoriasDAOService,
    protected daoActores: ActoresDAOService, private window: WindowService) {
    super(dao)
    // Soluciona el problema de las clases JavaScript por el cual los métodos pierden la referencia a this cuando se referencian por nombre (ExecPipe)
    this.dameActor = this.dameActor.bind(this)
    this.dameCategoria = this.dameCategoria.bind(this)
  }

  public override view(key: any): void {
    super.view(key, { params: new HttpParams().set('mode', 'details') })
  }

  public override delete(key: number, nextFn?: (hook?: boolean) => void): void {
    this.window.confirm('Una vez borrado no se podrá recuperar. ¿Continuo?',
      () => super.delete(key, nextFn), NotificationType.error, "Confirmación")
  }

  // Filtrado

  private filtro: any = { title: '', minlength: '', maxlength: '', rating: '' }
  public get Filtro(): any { return this.filtro; }
  public get HasFiltro(): boolean {
    return this.filtro.title !== '' || this.filtro.minlength !== '' || this.filtro.maxlength !== '' || this.filtro.rating !== ''
  }
  search() {
    (this.dao as PeliculasDAOService).search(this.filtro).subscribe({
      next: data => {
        this.listado = data;
        this.modo = 'list';
        this.totalRows = 0
      },
      error: err => this.handleError(err)
    })
  }

  protected override afterAdd(): void {
    this.elemento = { rating: 'G' }
    this.cargaListas()
  }
  protected override afterEdit(): void {
    this.cargaListas()
  }
  // Formularios

  public idiomas: any[] = [];
  public clasificaciones: any[] = [];
  public contenidos: any[] = [];
  private actores: any[] = [];
  private categorias: any[] = [];

  public get Actores(): any { return this.actores.filter(item => !this.elemento?.actors?.includes(item.id)); }
  public get Categorias(): any { return this.categorias.filter(item => !this.elemento?.categories?.includes(item.id)); }
  public get Contenidos(): any { return this.contenidos.filter(item => !this.elemento?.specialFeatures?.includes(item)); }
  public get Clasificaciones(): any {
    this.cargaClasificaciones();
    return this.clasificaciones;
  }

  public cargaCategorias() {
    this.daoCategorias.query().subscribe({
      next: data => {
        this.categorias = data;
        this.dameCategoria = this.dameCategoria.bind(this)
      },
      error: err => this.handleError(err)
    });
  }

  private cargaListas() {
    this.cargaClasificaciones();
    if (this.contenidos.length === 0)
      (this.dao as PeliculasDAOService).contenidos().subscribe({
        next: data => {
          this.contenidos = data;
        },
        error: err => this.handleError(err)
      });
    this.cargaCategorias();
    this.daoActores.query().subscribe({
      next: data => {
        this.actores = data;
        this.dameActor = this.dameActor.bind(this)
      },
      error: err => this.handleError(err)
    });
    this.daoIdiomas.query().subscribe({
      next: data => {
        this.idiomas = data;
      },
      error: err => this.handleError(err)
    });
  }


  private cargaClasificaciones() {
    if (this.clasificaciones.length === 0) {
      this.clasificaciones = [{ id: '', categoria: '' }];
      (this.dao as PeliculasDAOService).clasificaciones().subscribe({
        next: data => {
          this.clasificaciones = data;
        },
        error: err => this.handleError(err)
      });
    }
  }

  dameActor(id: number) {
    if (!this?.actores || this.actores.length === 0) return '(sin cargar)'
    return this.actores.find(item => item.id === id)?.nombre ?? 'error'
  }
  addActor(id: number) {
    if (!this.elemento.actors) {
      this.elemento.actors = []
    } else if (this.elemento.actors.includes(id)) {
      this.notify.add('Ya tiene la categoría')
      return
    }
    this.elemento.actors.push(id)
  }
  removeActor(index: number) {
    this.elemento.actors.splice(index, 1)
  }

  dameCategoria(id: number) {
    if (!this?.categorias || this.categorias.length === 0) return '(sin cargar)'
    return this.categorias.find(item => item.id === id)?.categoria ?? 'error'
  }
  addCategoria(id: number) {
    if (!this.elemento.categories) {
      this.elemento.categories = []
    } else if (this.elemento.categories.includes(id)) {
      this.notify.add('Ya tiene la categoría')
      return
    }
    this.elemento.categories.push(id)
  }
  removeCategoria(index: number) {
    this.elemento.categories.splice(index, 1)
  }

  public porCategorias(id: number) {
    this.cargaCategorias();
    this.daoCategorias.peliculas(id).subscribe({
      next: data => {
        this.listado = data;
      },
      error: err => this.handleError(err)
    });
  }

  addContenido(item: string) {
    if (!this.elemento.specialFeatures) {
      this.elemento.specialFeatures = []
    } else if (this.elemento.specialFeatures.includes(item)) {
      this.notify.add('Ya tiene el contenido')
      return
    }
    this.elemento.specialFeatures.push(item)
  }
  removeContenido(index: number) {
    this.elemento.specialFeatures.splice(index, 1)
  }

}
