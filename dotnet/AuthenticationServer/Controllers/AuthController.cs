﻿using System.Text.Json.Serialization;

namespace AuthenticationServer.Controllers;

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Security.Principal;
using System.Text;
using System.Text.Json.Serialization;
using static System.Runtime.InteropServices.JavaScript.JSType;

[ApiController]
[Route("[controller]")]
public class AuthController : ControllerBase {
    public static int EXPIRES_IN_MINUTES = 5;
    private readonly IConfiguration _configuration;
    private readonly List<Usuario> db = new List<Usuario> {
        new Usuario("adm@example.com", "P@$$w0rd", "Administrador", new List<string> { "Usuarios", "Administradores" }),
        new Usuario("usr@example.com", "P@$$w0rd", "Usuario registrado", new List<string> { "Usuarios" }),
        new Usuario("emp@example.com", "P@$$w0rd", "Empleado", new List<string> { "Usuarios", "Empleados" }),
    };
    private readonly SymmetricSecurityKey simetricKey;
    private readonly SigningCredentials simetricCredentials;
    private readonly SigningCredentials asimetricCredentials;

    public AuthController(IConfiguration configuration) {
        _configuration = configuration;
        simetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Refresh"])); // Usamos la clave simétrica
        simetricCredentials = new SigningCredentials(simetricKey, 
            SecurityAlgorithms.HmacSha256 // Especificamos el algoritmo HS256
            );
        asimetricCredentials = new SigningCredentials(
                new RsaSecurityKey(KeyManager.RsaPrivateKey), // Usamos la clave privada generada
                SecurityAlgorithms.RsaSha256 // Especificamos el algoritmo RS256
            );
    }

    [HttpPost("login")]
    public IActionResult Login([FromBody] LoginModel model) {
        var item = db.Find(u => u.IdUsuario == model.Username);
        if(item == null || !item.Active || item.Password != model.Password)
            return Ok(new AuthToken());
        return Ok(GenerateAuthToken(item));
    }

    [HttpPost("refresh")]
    public IActionResult refresh([FromBody] RefreshToken model) {
        var handler = new JwtSecurityTokenHandler();
        var tokenValidationParameters = new TokenValidationParameters {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = simetricKey,
            ValidateIssuer = true,
            ValidIssuer = _configuration["Jwt:Issuer"],
            ValidateAudience = true,
            ValidAudience = _configuration["Jwt:AudienceRefresh"],
            ValidateLifetime = true
        };
        try {
            var principal = handler.ValidateToken(model.Token, tokenValidationParameters, out var validatedToken);

            if(!principal.Identity.IsAuthenticated)
                return Forbid();
            var item = db.Find(u => u.IdUsuario == principal.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value);
            if(item == null || !item.Active)
                return Ok(new AuthToken());
            return Ok(GenerateAuthToken(item));
        } catch(SecurityTokenExpiredException) {
            return Ok(new AuthToken());
        }
    }

    [HttpGet("signature")]
    public IActionResult PublicKey() {
        return Ok(KeyManager.ExportPublicKeyToPEM());
    }

    [HttpGet("auth")]
    [Authorize]
    public IActionResult Auth() {
        var result = new {
            username = User.Claims.FirstOrDefault(e => e.Type == ClaimTypes.NameIdentifier)?.Value,
            roles = User.Claims.Where(e => e.Type == ClaimTypes.Role).Select(e => e.Value).ToList()
        };
        return Ok(result);        
    }

    private AuthToken GenerateAuthToken(Usuario usr) {
        var claims = new List<Claim> {
                new Claim(JwtRegisteredClaimNames.Sub, usr.IdUsuario),
            };
        claims.AddRange(usr.Roles.Select(e => new Claim("roles", e)).ToList());

        // Creamos un descriptor del token con las claims, expiración, emisor y audiencia
        var tokenDescriptor = new SecurityTokenDescriptor {
            Issuer = _configuration["Jwt:Issuer"],
            Audience = _configuration["Jwt:Audience"],
            Subject = new ClaimsIdentity(claims),
            IssuedAt = DateTime.UtcNow,
            Expires = DateTime.UtcNow.AddMinutes(EXPIRES_IN_MINUTES),

            // MUY IMPORTANTE: Firmamos el token con la CLAVE PRIVADA RSA y el algoritmo RS256
            SigningCredentials = asimetricCredentials
        };
        var tokenHandler = new JwtSecurityTokenHandler();
        var token = tokenHandler.WriteToken(tokenHandler.CreateToken(tokenDescriptor));

        return new AuthToken {
            Success = true,
            Name = usr.Nombre,
            Roles = usr.Roles,
            TokenType = "Bearer",
            AccessToken = token,
            RefreshToken = GenerateRefreshToken(usr),
            ExpiresIn = EXPIRES_IN_MINUTES
        };
    }
    private string GenerateRefreshToken(Usuario usr) {
        var claims = new List<Claim> {
                new Claim(JwtRegisteredClaimNames.Sub, usr.IdUsuario),
            };

        // Creamos un descriptor del token con las claims, expiración, emisor y audiencia
        var tokenDescriptor = new SecurityTokenDescriptor {
            Issuer = _configuration["Jwt:Issuer"],
            Audience = _configuration["Jwt:AudienceRefresh"],
            Subject = new ClaimsIdentity(claims),
            IssuedAt = DateTime.UtcNow,
            Expires = DateTime.UtcNow.AddMinutes(EXPIRES_IN_MINUTES * 4),
            NotBefore = DateTime.UtcNow.AddMinutes(EXPIRES_IN_MINUTES),

            // MUY IMPORTANTE: Firmamos el token con la SIMETRICA y el algoritmo HS256
            SigningCredentials = simetricCredentials
        };
        var tokenHandler = new JwtSecurityTokenHandler();
        return tokenHandler.WriteToken(tokenHandler.CreateToken(tokenDescriptor));
    }
}
public class LoginModel {
    public string Username { get; set; }
    public string Password { get; set; }
}

public class AuthToken {
    public bool Success { get; set; } = false;
    [JsonPropertyName("token_type")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string TokenType { get; set; }
    [JsonPropertyName("access_token")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string AccessToken { get; set; }
    [JsonPropertyName("refresh_token")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string RefreshToken { get; set; }
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public string Name { get; set; }
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public List<string> Roles { get; set; }
    [JsonPropertyName("expires_in")]
    [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingDefault)]
    public int ExpiresIn { get; set; }
}

public class RefreshToken {
    public string Token { get; set; }
}

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
