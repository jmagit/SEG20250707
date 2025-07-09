using System.Text.Json.Serialization;

namespace AuthenticationServer.Entities;

public class Usuario {
    public string IdUsuario { get; set; }
    public string Password { get; set; }
    public string Nombre { get; set; }
    public List<string> Roles { get; set; } = new List<string>();
    public bool Active { get; set; } = true;

    public Usuario(string idUsuario, string password, string nombre, List<string> roles, bool active = true) {
        IdUsuario = idUsuario;
        Password = password;
        Nombre = nombre;
        Roles = roles;
        Active = active;
    }
}
