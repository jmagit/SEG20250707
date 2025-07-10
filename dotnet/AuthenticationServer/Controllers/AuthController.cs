namespace AuthenticationServer.Controllers;

using AuthenticationServer.Entities;
using AuthenticationServer.Models;
using AuthenticationServer.Security;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;

[ApiController]
[Route("[controller]")]
public class AuthController : ControllerBase {
    public static int EXPIRES_IN_MINUTES = 5;
    private readonly IConfiguration _configuration;
    private readonly List<Usuario> db = new List<Usuario> {
        // Contraseña: P@$$w0rd
        new Usuario("adm@example.com", "$2a$10$HyqduzZnjWC0ittWFTNWnOaagOwLXusQelfQ4TBwjXx1bMm8.sMDe", "Administrador", new List<string> { "Usuarios", "Administradores" }),
        new Usuario("usr@example.com", "$2a$10$wmj8GP0PlEJkuezu6zZhNu7F3O7l7G1a2nTBdy231oDUT4h67.Koq", "Usuario registrado", new List<string> { "Usuarios" }),
        new Usuario("emp@example.com", "$2a$10$uk8teFJl3.e8Glh2rAYMb.4H8aRD9/.xUgzeog/wvgGIT6mBGGTra", "Empleado", new List<string> { "Usuarios", "Empleados" }),
    };
    private readonly SymmetricSecurityKey simetricKey;
    private readonly SigningCredentials simetricCredentials;
    private readonly SigningCredentials asimetricCredentials;

    public AuthController(IConfiguration configuration) {
        _configuration = configuration;
        simetricKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_configuration["Jwt:Refresh"] ?? "")); // Usamos la clave simétrica
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
        if(item == null || !item.Active || !BCrypt.Net.BCrypt.Verify(model.Password, item.Password))
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

            if(!principal.Identity?.IsAuthenticated ?? false)
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

