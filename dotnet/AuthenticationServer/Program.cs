using AuthenticationServer.Security;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.OpenApi;
using Microsoft.OpenApi.Models;

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
                .AddJwtBearer(SecutityManager.DefaultConfig(builder));
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
