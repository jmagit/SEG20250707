using AuthenticationServer.Utils;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.OpenApi;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System.Net;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace AuthenticationServer {
    public class Program {
        public static void Main(string[] args) {
            var builder = WebApplication.CreateBuilder(args);

            // Add services to the container.

            builder.Services.AddControllers();
            //    .AddJsonOptions(options => {
            //    // Configura para ignorar propiedades con valor null O valor por defecto
            //    options.JsonSerializerOptions.DefaultIgnoreCondition = JsonIgnoreCondition.WhenWritingDefault;
            //})

            builder.Services.AddOpenApi(options => {
                options.AddDocumentTransformer((document, context, cancellationToken) => {
                    document.Info = new() {
                        Title = "Microservicios: Servidor de Autenticación", Version = "v1", Description = "Ejemplo de un servidor de autenticación con JWT Bearer"
                    };
                    return Task.CompletedTask;
                });
                options.AddDocumentTransformer<BearerSecuritySchemeTransformer>();
            });
            builder.Services.AddCors(options => {
                options.AddPolicy("AllowAll", builder => builder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
            });

            //KeyManager.GenerateRsaKeys();
            KeyManager.LoadRsaKeys(builder.Configuration["Jwt:PublicKey"], builder.Configuration["Jwt:PrivateKey"]);

            // Configuración de la autenticación JWT Bearer
            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(options => {
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

                });
            builder.Services.AddAuthorization(); // Habilita la autorización para las pruebas

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if(app.Environment.IsDevelopment()) {
            }

            app.MapOpenApi();
            app.UseSwaggerUI(options => {
                options.SwaggerEndpoint("/openapi/v1.json", "v1");
            });

            app.UseCors("AllowAll");

            app.UseAuthentication(); // Debe ir antes de UseAuthorization
            app.UseAuthorization();


            app.MapControllers();

            app.Run();
        }
    }
    // Agregar la cabecera info al documento OpenApi
    internal sealed class BearerSecuritySchemeTransformer(IAuthenticationSchemeProvider authenticationSchemeProvider) : IOpenApiDocumentTransformer {
        public async Task TransformAsync(OpenApiDocument document, OpenApiDocumentTransformerContext context, CancellationToken cancellationToken) {
            var authenticationSchemes = await authenticationSchemeProvider.GetAllSchemesAsync();
            if(authenticationSchemes.Any(authScheme => authScheme.Name == "Bearer")) {
                var requirements = new Dictionary<string, OpenApiSecurityScheme> {
                    ["Bearer"] = new OpenApiSecurityScheme {
                        Type = SecuritySchemeType.Http,
                        Scheme = "bearer", // "bearer" refers to the header name here
                        In = ParameterLocation.Header,
                        BearerFormat = "Json Web Token"
                    }
                };
                document.Components ??= new OpenApiComponents();
                document.Components.SecuritySchemes = requirements;
            }
        }
    }
}
