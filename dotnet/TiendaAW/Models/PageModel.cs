using System.ComponentModel;

namespace TiendaAW.Models;

[Description("Resultado de la paginación")]
public class PageModel<T> {
    public int Number { get; set; }
    /// <summary>
    /// Número de elementos en la página actual
    /// </summary>
    public int Size { get; set; }
    public int TotalElements { get; set; }
    public int TotalPages { get; set; }
    public List<T> Content { get; set; }

}

