import { CommonModule } from '@angular/common';
import { Component, Injectable, input, inputBinding, output, outputBinding, ViewContainerRef } from '@angular/core';
import { NotificationType } from './notification-service';

@Component({
  selector: 'app-emergente',
  standalone: true, // ¡Este es un componente standalone!
  imports: [CommonModule],
  template: `
    <style>
      .fondo-sombreado {
        position: fixed;
        background-color: black;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        opacity: 0.3;
      }
    </style>
    <div class="fondo-sombreado"></div>
    <div class="modal fade show" [style.display]="'block'" tabindex="-1" >
      <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
          <div class="modal-header bg-gradient text-white" [ngClass]="{
                  'bg-danger': type() === NotificationType.error,
                  'bg-warning': type() === NotificationType.warn,
                  'bg-primary': type() === NotificationType.info,
                  'bg-success': type() === NotificationType.log
                }">
            <h5 class="modal-title" id="exampleModalLabel">{{title()}}</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
              (click)="confirmar()"></button>
          </div>
          <div class="modal-body bg-light bg-gradient p-0">
            <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
              <symbol id="check-circle-fill" fill="currentColor" viewBox="0 0 16 16">
                <path
                  d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zm-3.97-3.03a.75.75 0 0 0-1.08.022L7.477 9.417 5.384 7.323a.75.75 0 0 0-1.06 1.06L6.97 11.03a.75.75 0 0 0 1.079-.02l3.992-4.99a.75.75 0 0 0-.01-1.05z" />
              </symbol>
              <symbol id="info-fill" fill="currentColor" viewBox="0 0 16 16">
                <path
                  d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm.93-9.412-1 4.705c-.07.34.029.533.304.533.194 0 .487-.07.686-.246l-.088.416c-.287.346-.92.598-1.465.598-.703 0-1.002-.422-.808-1.319l.738-3.468c.064-.293.006-.399-.287-.47l-.451-.081.082-.381 2.29-.287zM8 5.5a1 1 0 1 1 0-2 1 1 0 0 1 0 2z" />
              </symbol>
              <symbol id="exclamation-triangle-fill" fill="currentColor" viewBox="0 0 16 16">
                <path
                  d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z" />
              </symbol>
              <symbol id="forbiben-circle-fill" fill="currentColor" viewBox="0 0 16 16">
                <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM4.5 7.5a.5.5 0 0 0 0 1h7a.5.5 0 0 0 0-1h-7z" />
              </symbol>
            </svg>
            @switch (type()) {
            @case (NotificationType.error) {
            <div class="m-0 p-3 alert alert-danger d-flex align-items-center rounded-0" role="alert">
              <svg class="bi flex-shrink-0 me-2 text-danger" width="24" height="24" role="img" aria-label="Danger:">
                @if(isConfirm()){
                <use xlink:href="#exclamation-triangle-fill" />
                } @else {
                <use xlink:href="#forbiben-circle-fill" />
                }
              </svg>
              <div>{{message()}}</div>
            </div>
            }
            @case (NotificationType.warn) {
            <div class="m-0 p-3 alert alert-warning d-flex align-items-center rounded-0" role="alert">
              <svg class="bi flex-shrink-0 me-2 text-warning" width="24" height="24" role="img" aria-label="Danger:">
                <use xlink:href="#exclamation-triangle-fill" />
              </svg>
              <div>{{message()}}</div>
            </div>
            }
            @case (NotificationType.info) {
            <div class="m-0 p-3 alert alert-primary d-flex align-items-center rounded-0" role="alert">
              <svg class="bi flex-shrink-0 me-2 text-primary" width="24" height="24" role="img" aria-label="Info:">
                @if(isConfirm()){
                <use xlink:href="#exclamation-triangle-fill" />
                } @else {
                <use xlink:href="#info-fill" />
                }
              </svg>
              <div>{{message()}}</div>
            </div>
            }
            @default {
            <div class="m-0 p-3 alert alert-success d-flex align-items-center rounded-0" role="alert">
              <svg class="bi flex-shrink-0 me-2 text-success" width="24" height="24" role="img" aria-label="Log:">
                @if(isConfirm()){
                <use xlink:href="#exclamation-triangle-fill" />
                } @else {
                <use xlink:href="#check-circle-fill" />
                }
              </svg>
              <div>{{message()}}</div>
            </div>
            }
            }
          </div>
          <div class="modal-footer bg-light bg-gradient">
            @if(isConfirm()){
              <button type="button" class="btn btn-secondary" (click)="confirmar(true)">{{acceptMsg()}}</button>
            }
            <button type="button" class="btn btn-secondary" (click)="confirmar()">{{cancelMsg()}}</button>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class ConfirmComponent {
  public readonly NotificationType = NotificationType;
  title = input('Confirmación')
  message = input('¿Estas seguro')
  acceptMsg = input('Si')
  cancelMsg = input('No')
  isConfirm = input(true)
  type = input(NotificationType.info)
  confirm = output<boolean>();

  confirmar(respuesta = false) {
    this.confirm.emit(respuesta)
  }
}


@Injectable({
  providedIn: 'root'
})
export class WindowService {
  private rootViewContainerRef: ViewContainerRef | null = null;

  get RootViewContainerRef(): ViewContainerRef {
    if (!this.rootViewContainerRef) {
      throw new Error('ViewContainerRef no está inicializado');
    }
    return this.rootViewContainerRef;
  }
  set RootViewContainerRef(viewContainerRef: ViewContainerRef) {
    this.rootViewContainerRef = viewContainerRef;
  }

  alert(message: string, tipo?: NotificationType, titulo?: string, cerrar?: string) {
    const binds = [
      inputBinding('message', () => message),
      inputBinding('isConfirm', () => false),
      outputBinding('confirm', () => {
        dialogo.destroy(); // Destruir después del cerrar
      })
    ]
    binds.push(inputBinding('title', () => titulo ?? 'Aviso'))
    binds.push(inputBinding('cancelMsg', () => cerrar ?? 'Cerrar'))
    if (tipo) binds.push(inputBinding('type', () => tipo))
    const dialogo = this.RootViewContainerRef.createComponent(ConfirmComponent, {
      index: 0,
      bindings: binds
    });
  }

  confirm(message: string, callback: VoidFunction, tipo?: NotificationType, titulo?: string, aceptar?: string,
    cancelar?: string) {
    const binds = [
      inputBinding('message', () => message),
      inputBinding('isConfirm', () => true),
      outputBinding('confirm', (result: boolean) => {
        if (result)
          callback()
        dialogo.destroy(); // Destruir después del cerrar
      })
    ]
    if (titulo) binds.push(inputBinding('title', () => titulo))
    if (aceptar) binds.push(inputBinding('acceptMsg', () => aceptar))
    if (cancelar) binds.push(inputBinding('cancelMsg', () => cancelar))
    if (tipo) binds.push(inputBinding('type', () => tipo))
    const dialogo = this.RootViewContainerRef.createComponent(ConfirmComponent, {
      index: 0,
      bindings: binds
    });
  }

  reload() {
    window.location.reload();
  }

}
