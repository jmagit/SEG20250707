import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Categorias } from './componente';

export const routes: Routes = [
  { path: '', component: Categorias },
];

@NgModule({
  declarations: [ ],
  exports: [
    RouterModule
  ],
  imports: [
    RouterModule.forChild(routes), Categorias,
  ]
})
export default class CategoriasModule { }
