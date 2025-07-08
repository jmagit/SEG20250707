/* eslint-disable @angular-eslint/component-selector */
import { Component, model } from '@angular/core';

@Component({
  selector: 'my-sizer',
  standalone: true,
  template: `
    <div [style.font-size.px]="size()">
      <button (click)="dec()">-</button>
      <output>FontSize: {{size()}}px</output>
      <button (click)="inc()">+</button>
    </div>
  `,
  host: { 'role': 'slider', '[attr.aria-valuenow]': 'size' }
})
export class Sizer {
  size = model(12);

  dec() : void { this.resize(-1); }
  inc() : void { this.resize(+1); }

  resize(delta: number) : void {
    this.size.update(previus => Math.min(40, Math.max(8, +previus + delta)))
  }
}
