import { Injectable, OnDestroy } from '@angular/core';
import { LoggerService } from '@my/library';
import { Subject } from 'rxjs';
import { environment } from 'src/environments/environment';

export enum NotificationType {
  error = 'error',
  warn = 'warn',
  info = 'info',
  log = 'log'
}

export class Notification {
  constructor(private id: number, private message: string,
    private type: NotificationType) { }
  public get Id() { return this.id; }
  public get Message() { return this.message; }
  public get Type() { return this.type; }
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService implements OnDestroy {
  public readonly NotificationType = NotificationType;
  private listado: Notification[] = [];
  private notificacion$ = new Subject<Notification>();

  constructor(private out: LoggerService) { }

  public get Listado(): Notification[] { return Object.assign([], this.listado); }
  public get HayNotificaciones() { return this.listado.length > 0; }
  public get Notificacion() { return this.notificacion$; }

  public add(msg: string, type: NotificationType = NotificationType.error) {
    if (!msg || msg === '') {
      this.out.error('Falta el mensaje de notificación.');
      return;
    }
    const id = this.HayNotificaciones ?
      (this.listado[this.listado.length - 1].Id + 1) : 1;
    const n = new Notification(id, msg, type);
    this.listado = [...this.listado, n];
    this.notificacion$.next(n);
    // Redundancia: Los errores también se muestran en consola
    if (!environment.production && type === NotificationType.error) {
      this.out.error(`NOTIFICATION: ${msg}`);
    }
  }
  public remove(index: number) {
    if (index < 0 || index >= this.listado.length) {
      this.out.error('Index out of range.');
      return;
    }
    this.listado = this.listado.filter((item, ind) => ind !== index);
  }
  public removeById(id: number) {
    this.listado = this.listado.filter((item) => item.Id !== id);
  }

  public clear() {
    if (this.HayNotificaciones)
      this.listado = [];
  }

  ngOnDestroy(): void {
    this.notificacion$.complete()
  }
}
