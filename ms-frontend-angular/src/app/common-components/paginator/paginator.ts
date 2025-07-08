/* eslint-disable @angular-eslint/no-output-rename */
/* eslint-disable @angular-eslint/no-input-rename */
import { Component, input, output, Signal, computed } from '@angular/core';

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.html',
  standalone: true,
  imports: []
})
export class Paginator {
  public readonly paginaActual = input.required<number>({ alias: 'current-page' })
  public readonly numeroTotalDePaginas = input.required<number>({ alias: 'total-pages' })
  public readonly numeroDePaginasVisibles = input<number>(20, { alias: 'max-pages-visibles' })

  public readonly pageChange = output<number>({ alias: 'page-change' })

  protected paginas: Signal<number[]> = computed(() => {
    const paginas: number[] = []
    let primeraPaginaVisible = 0;
    let ultimaPaginaVisible = this.numeroTotalDePaginas();
    if (ultimaPaginaVisible > this.numeroDePaginasVisibles()) {
      const mitadDelNumeroDePaginasVisibles = Math.floor(this.numeroDePaginasVisibles() / 2);
      primeraPaginaVisible = Math.max(0, this.paginaActual() - mitadDelNumeroDePaginasVisibles + 1);
      ultimaPaginaVisible = Math.min(this.numeroTotalDePaginas(), this.paginaActual() + mitadDelNumeroDePaginasVisibles);
      // Ajustar el rango si está cerca del principio o del final
      if (ultimaPaginaVisible - primeraPaginaVisible < this.numeroDePaginasVisibles()) {
        if (primeraPaginaVisible === 0) {
          ultimaPaginaVisible = Math.min(this.numeroTotalDePaginas(), primeraPaginaVisible + this.numeroDePaginasVisibles());
        } else if (ultimaPaginaVisible === this.numeroTotalDePaginas()) {
          primeraPaginaVisible = Math.max(1, this.numeroTotalDePaginas() - this.numeroDePaginasVisibles());
        }
      }
    }
    if (primeraPaginaVisible > 0) paginas.push(-1) // mostrar elipsis inicial
    for (let i = primeraPaginaVisible; i < ultimaPaginaVisible; paginas.push(i++));
    if (ultimaPaginaVisible < this.numeroTotalDePaginas()) paginas.push(-2) // mostrar elipsis final
    return paginas;
  })
}
