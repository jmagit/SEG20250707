import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { LoggerService } from '@my/library';
import { NotificationService, NavigationService } from '../common-services';
import { AuthService } from '../security';
import { CancelOperationArg, ModoCRUD } from './tipos';
import { RESTDAOService } from './rest-dao-service.class';
import { inject } from '@angular/core';

export abstract class ViewModelService<T, K> {
  protected modo: ModoCRUD = 'list';
  protected listado: T[] = [];
  protected elemento: T | null = null;
  protected idOriginal: K | null = null;
  public out = inject(LoggerService)
  public router = inject(Router)
  public navigation = inject(NavigationService)
  public notify = inject(NotificationService)
  public auth = inject(AuthService)

  constructor(protected dao: RESTDAOService<T, K>) {
  }

  public get Modo(): ModoCRUD { return this.modo; }
  public get Listado(): T[] { return this.listado; }
  public get Elemento(): T { return this.elemento ?? {} as T; }

  //#region Operaciones CRUD
  public list(extras = {}): void {
    const arg = new CancelOperationArg()
    this.beforeList(arg)
    if (arg.isCancel) return;
    this.dao.query(extras).subscribe({
      next: data => {
        this.listado = data;
        this.modo = 'list';
        this.afterList()
      },
      error: err => this.handleError(err)
    });
  }
  public add(): void {
    const arg = new CancelOperationArg()
    this.beforeAdd(arg)
    if (arg.isCancel) return;
    if (!this.elemento) this.elemento = {} as T
    this.modo = 'add';
    this.afterAdd()
  }
  public edit(key: K, extras = {}): void {
    const arg = new CancelOperationArg()
    this.beforeEdit(arg)
    if (arg.isCancel) return;
    this.dao.get(key, extras).subscribe({
      next: data => {
        this.elemento = data;
        this.idOriginal = key;
        this.modo = 'edit';
        this.afterEdit()
      },
      error: err => this.handleError(err)
    });
  }
  public view(key: K, extras = {}): void {
    const arg = new CancelOperationArg()
    this.beforeView(arg)
    if (arg.isCancel) return;
    this.dao.get(key, extras).subscribe({
      next: data => {
        this.elemento = data;
        this.modo = 'view';
        this.afterView()
      },
      error: err => this.handleError(err)
    });
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  public delete(key: K, nextFn: (arg?: any) => void = this.list): void {
    const arg = new CancelOperationArg()
    this.beforeDelete(arg)
    if (arg.isCancel) return;

    this.dao.remove(key).subscribe({
      next: () => {
        nextFn.apply(this)
      },
      error: err => this.handleError(err)
    });
  }
  //#endregion

  //#region Operaciones Send y Cancel
  clear() {
    this.elemento = null;
    this.idOriginal = null;
    this.listado = [];
  }

  public cancel(hook = true): void {
    if(hook) {
      const arg = new CancelOperationArg()
      this.beforeCancel(arg)
      if (arg.isCancel) return;
    }
    this.clear();
    this.navigation.back()
    if(hook)
      this.afterSend()
  }
  public send(): void {
    if(!this.elemento) return
    const arg = new CancelOperationArg()
    this.beforeSend(arg)
    if (arg.isCancel) return;
    switch (this.modo) {
      case 'add':
          this.dao.add(this.elemento).subscribe({
            next: () => {
              this.afterSend()
              this.cancel(false)
            },
            error: err => this.handleError(err)
          });
        break;
      case 'edit':
        if (this.idOriginal)
          this.dao.change(this.idOriginal, this.elemento).subscribe({
            next: () => {
              this.afterSend()
              this.cancel(false)
            },
            error: err => this.handleError(err)
          });
        break;
      case 'view':
        this.cancel();
        break;
    }
  }
  //#endregion

  //#region Tratamiento de errores
  handleError(err: HttpErrorResponse) {
    let msg = ''
    switch (err.status) {
      case 0: msg = err.message; break;
      case 404: msg = `ERROR: ${err.status} ${err.statusText}`; break;
      default:
        msg = err.error?.['detail'] ?? err.error?.['title'] ?? ''
        msg = `ERROR: ${err.status} ${err.statusText}.${msg ? ` Detalles: ${msg}` : ''}`
        if (err.error?.['errors']) {
          for (const cmp in err.error?.['errors'])
            msg += ` ${cmp}: ${err.error?.['errors'][cmp]}.`
        }
        break;
    }
    this.notify.add(msg)
  }
  imageErrorHandler(event: Event, _item?: T) {
    (event.target as HTMLImageElement).src = '/images/photo-not-found.svg'
  }
  //#endregion

  //#region Hooks para sobre escribir
  protected beforeList(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterList() {
    // Para sobre escribir
  }
  protected beforeAdd(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterAdd() {
    // Para sobre escribir
  }
  protected beforeEdit(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterEdit() {
    // Para sobre escribir
  }
  protected beforeView(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterView() {
    // Para sobre escribir
  }
  protected beforeDelete(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterDelete() {
    // Para sobre escribir
  }
  protected beforeSend(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterSend() {
    // Para sobre escribir
  }
  protected beforeCancel(_cancel: CancelOperationArg) {
    // Para sobre escribir
  }
  protected afterCancel() {
    // Para sobre escribir
  }
  //#endregion
}

export abstract class ViewModelPagedService<T, K> extends ViewModelService<T, K> {
  public page = 0;
  public totalPages = 0;
  public totalRows = 0;
  public rowsPerPage = 14;
  public orderBy?: string;

  constructor(dao: RESTDAOService<T, K>) {
    super(dao)
  }

  load(page: number = -1) {
    if (!page || page < 0) page = this.page;
    this.dao.page(page, this.rowsPerPage, this.orderBy).subscribe({
      next: rslt => {
        this.page = rslt.page;
        this.totalPages = rslt.pages;
        this.totalRows = rslt.rows;
        this.listado = rslt.list;
        this.modo = 'list';
      },
      error: err => this.handleError(err)
    })
  }

  pageChange(page: number = 0) {
    this.router.navigate([], { queryParams: { page } })
  }
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  override delete(key: K, nextFn: (arg?: any) => void = this.load): void {
      super.delete(key, nextFn)
  }
}
