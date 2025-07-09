
using AuthenticationServer.Security;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using TiendaAW.Data;

namespace TiendaAW {
    public class Program {
        public static void Main(string[] args) {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddDbContext<AdventureWorksContext>(options =>
                options.UseSqlServer(builder.Configuration.GetConnectionString("AdventureWorksConnection"))
                );

            builder.Services.AddDatabaseDeveloperPageExceptionFilter();


            builder.Services.AddControllers();
            // Learn more about configuring OpenAPI at https://aka.ms/aspnet/openapi
            builder.Services.AddOpenApi();

            // Configuración de la autenticación JWT Bearer
            builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
                .AddJwtBearer(SecutityManager.DefaultConfig(builder));
            builder.Services.AddAuthorization(); // Habilita la autorización para las pruebas

            builder.Services.AddSwaggerGen(option => {
                option.SwaggerDoc("v1", new() {
                    Title = "Demos del curso", Version = "v1", Description = "Ejemplo de ASP.NET Core Web API"
                });

                option.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme {
                    In = ParameterLocation.Header, // Indica que el token se espera en la cabecera HTTP
                    Description = "Por favor, introduce un token JWT válido con el prefijo 'Bearer ' (Ej: 'Bearer eyJhbGciOiJIUzI1Ni...').",
                    Name = "Authorization", // El nombre de la cabecera HTTP que contendrá el token
                    Type = SecuritySchemeType.Http, // Para esquemas HTTP como Bearer
                    Scheme = "Bearer", // El esquema de autenticación (Bearer para JWT)
                    BearerFormat = "JWT" // Opcional: para documentación, indica que es un JWT
                });

                option.AddSecurityRequirement(new OpenApiSecurityRequirement {{ new OpenApiSecurityScheme{
                    Reference = new OpenApiReference { Type = ReferenceType.SecurityScheme, Id = "Bearer" }
                }, new string[] { } } });
            });

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if(app.Environment.IsDevelopment()) {
                app.MapOpenApi();
            }
            app.UseSwagger(); // Sirve el archivo swagger.json/openapi.json
            app.UseSwaggerUI(options => {
                options.RoutePrefix = string.Empty;
                options.SwaggerEndpoint("/swagger/v1/swagger.json", "Demos del curso");
            }); // Sirve la interfaz de usuario (opcional, pero común)

            //app.UseAuthentication(); // Debe ir antes de UseAuthorization
            app.UseAuthorization();


            app.MapControllers();

            //app.MapGet("/", () => redi)

            app.Run();
        }
    }
}
