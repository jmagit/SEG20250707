import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Idiomas } from './componente';

export const routes: Routes = [
  { path: '', component: Idiomas },
];

@NgModule({
  declarations: [ ],
  exports: [
    RouterModule
  ],
  imports: [
    RouterModule.forChild(routes), Idiomas,
  ]
})
export default class IdiomasModule { }
