import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Card } from './card';
import { FormButtons } from './form-buttons/form-buttons';
import { ListButtons } from './list-buttons';

@NgModule({
  declarations: [],
  exports: [ ListButtons, Card, FormButtons, ],
  imports: [
    CommonModule, ListButtons, Card, FormButtons,
  ]
})
export class CommonComponentsModule { }
