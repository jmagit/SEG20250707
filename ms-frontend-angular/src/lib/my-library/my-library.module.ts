import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PIPES_CADENAS } from './pipes/cadenas.pipe';
import { Sizer } from './components/sizer';
import { PIPES_NUMERICOS } from './pipes/numericos.pipe';
import { MIS_VALIDADORES } from './directives/mis-validadores';
import { VALIDATORS_DATES } from './directives/dates-validators';



@NgModule({
  declarations: [ ],
  imports: [
    CommonModule, PIPES_CADENAS, PIPES_NUMERICOS, MIS_VALIDADORES, VALIDATORS_DATES, Sizer,
  ],
  exports: [ PIPES_CADENAS, PIPES_NUMERICOS, MIS_VALIDADORES, VALIDATORS_DATES, Sizer, ],
})
export class MyLibraryModule { }
