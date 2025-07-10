using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

namespace AuthenticationServer.Controllers;

[ApiController]
[Route("[controller]")]
public class TestController : ControllerBase {

    [HttpGet("status")]
    public IActionResult Status() {
        if(!User.Identity?.IsAuthenticated ?? false)
            return Ok("No autenticado");
        var result = new {
            username = User.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value,
            roles = User.Claims.Where(e => e.Type == ClaimTypes.Role).Select(e => e.Value).ToList()
        };
        return Ok(result);
    }

    [HttpGet("solo-autenticados")]
    [Authorize]
    public IActionResult SoloAutenticados() {
        return Ok($"Usuario autenticado como {User.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value}.");
    }

    [HttpGet("solo-administradores")]
    [Authorize(Roles = "Administradores")]
    public IActionResult SoloAdministradores() {
        return Ok($"Usuario administrador como {User.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value}.");
    }

    [HttpGet("solo-empleados")]
    [Authorize(Roles = "Empleados")]
    public IActionResult SoloEmpleados() {
        return Ok($"Usuario es el empleado {User.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value}.");
    }

    [HttpGet("password/encode")]
    public IActionResult pwdEncode(string password) {
        return Ok(BCrypt.Net.BCrypt.HashPassword(password));
    }

    [HttpGet("password/verify")]
    public IActionResult pwdVerify(string password, string stored) {
        return Ok(BCrypt.Net.BCrypt.Verify(password, stored) ? "OK" : "KO");
    }
}

