using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;

namespace AuthenticationServer.Security;

public static class SecutityManager {
    public static Action<JwtBearerOptions> DefaultConfig(WebApplicationBuilder builder) {
        if(KeyManager.RsaPublicKey == null)
            KeyManager.LoadRsaKeys(builder.Configuration["Jwt:PublicKey"]);

        return options => {
            options.TokenValidationParameters = new TokenValidationParameters {
                ValidateIssuer = true, // Valida el emisor del token
                ValidateAudience = true, // Valida la audiencia del token
                ValidateLifetime = true, // Valida la fecha de expiración del token
                ValidateIssuerSigningKey = true, // MUY IMPORTANTE: Valida la firma del token

                // Obtener la configuración desde appsettings.json
                ValidIssuer = builder.Configuration["Jwt:Issuer"],
                ValidAudience = builder.Configuration["Jwt:Audience"],
                // Especificamos la clave pública para la validación de la firma (RS256)
                IssuerSigningKey = new RsaSecurityKey(KeyManager.RsaPublicKey),

                ClockSkew = TimeSpan.Zero // Elimina la tolerancia de tiempo por defecto (5 minutos)
            };
            options.Events = new JwtBearerEvents {
                OnChallenge = async context => {
                    // Si la autenticación falla, puedes personalizar el encabezado WWW-Authenticate
                    context.HandleResponse();
                    context.Response.Headers["WWW-Authenticate"] = "Bearer realm=\"MicroserviciosJWT\"";
                    context.Response.StatusCode = (int)HttpStatusCode.Unauthorized;
                    context.Response.ContentType = "application/problem+json";

                    var problemDetails = new ProblemDetails {
                        Status = (int)HttpStatusCode.Unauthorized,
                        Type = "https://tools.ietf.org/html/rfc7807#section-3.1", // URI para errores de autenticación
                        Title = "Unauthorized",
                        Detail = "Necesitas un token de autenticación válido para acceder a este recurso.",
                        Instance = context.Request.Path // La ruta de la solicitud que causó el error
                    };

                    if(!string.IsNullOrEmpty(context.ErrorDescription)) {
                        problemDetails.Detail = $"Necesitas un token de autenticación válido. Detalles: {context.ErrorDescription}";
                        context.Response.Headers["WWW-Authenticate"] = $"Bearer realm=\"MicroserviciosJWT\", error=\"invalid_token\", error_description=\"{context.ErrorDescription}\"";
                    } else if(!string.IsNullOrEmpty(context.Error)) {
                        problemDetails.Detail = $"Necesitas un token de autenticación válido. Error: {context.Error}";
                    }

                    await JsonSerializer.SerializeAsync(context.Response.Body, problemDetails, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });

                },
                OnForbidden = async context => {
                    context.Response.StatusCode = (int)HttpStatusCode.Forbidden;
                    context.Response.ContentType = "application/problem+json";
                    context.Response.Headers["WWW-Authenticate"] = $"Bearer realm=\"MicroserviciosJWT\", error=\"insufficient_scope\", error_description=\"Requires higher privileges\"";

                    var problemDetails = new ProblemDetails {
                        Status = (int)HttpStatusCode.Forbidden,
                        Type = "https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.3", // URI para errores de autorización
                        Title = "Forbidden",
                        Detail = "No tienes los permisos necesarios para acceder a este recurso.",
                        Instance = context.Request.Path
                    };

                    await JsonSerializer.SerializeAsync(context.Response.Body, problemDetails, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });
                },

            };

        };
    }
}
public static class KeyManager {
    // Las claves pública y privada deben ser accesibles globalmente o de inyección de dependencias.
    public static RSA RsaPrivateKey { get; private set; }
    public static RSA RsaPublicKey { get; private set; }

    public static void GenerateRsaKeys(int keySize = 2048) {
        using(var rsa = RSA.Create(keySize)) {
            // Exportar la clave pública
            RsaPublicKey = RSA.Create();
            RsaPublicKey.ImportParameters(rsa.ExportParameters(false)); // false para clave pública

            // Exportar la clave privada
            RsaPrivateKey = RSA.Create();
            RsaPrivateKey.ImportParameters(rsa.ExportParameters(true)); // true para clave privada
        }
    }
    public static void LoadRsaKeys(string publicKey, string privateKey = null) {
        RsaPublicKey = RSA.Create();
        RsaPublicKey.ImportFromPem(ToPEM(publicKey).ToCharArray());
        if(privateKey != null) {
            RsaPrivateKey = RSA.Create();
            RsaPrivateKey.ImportFromPem(ToPEM(privateKey, false).ToCharArray());
        }
    }

    public static string ExportPublicKeyToPEM() {
        var publicKeyBytes = RsaPublicKey.ExportSubjectPublicKeyInfo();
        var base64 = Convert.ToBase64String(publicKeyBytes);
        return ToPEM(base64);
    }

    public static string ToPEM(string base64, bool isPublic = true) {
        var builder = new StringBuilder();
        builder.AppendLine($"-----BEGIN {(isPublic ? "PUBLIC" : "PRIVATE")} KEY-----");
        for(int i = 0; i < base64.Length; i += 64) {
            builder.AppendLine(base64.Substring(i, Math.Min(64, base64.Length - i)));
        }
        builder.AppendLine($"-----END {(isPublic ? "PUBLIC" : "PRIVATE")} KEY-----");
        return builder.ToString();
    }

    // En un escenario real, se cargarían estas claves desde un certificado X.509
    // Por ejemplo:
    // public static RsaSecurityKey GetSigningSecurityKeyFromCertificate(string certificatePath, string password)
    // {
    //     var certificate = new System.Security.Cryptography.X509Certificates.X509Certificate2(certificatePath, password);
    //     return new RsaSecurityKey(certificate.GetRSAPrivateKey());
    // }
    //
    // public static RsaSecurityKey GetValidationSecurityKeyFromCertificate(string certificatePath)
    // {
    //     var certificate = new System.Security.Cryptography.X509Certificates.X509Certificate2(certificatePath);
    //     return new RsaSecurityKey(certificate.GetRSAPublicKey());
    // }
}
