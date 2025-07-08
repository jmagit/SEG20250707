import { Component, input } from '@angular/core';

@Component({
    selector: 'app-avatar',
    template: `
    <img class="rounded-4" [src]="foto()" [alt]="titulo()" width="300" height="200">
  `,
    standalone: true,
})
export class Avatar {
  public foto = input.required<string>()
  public titulo = input.required<string>()
}
