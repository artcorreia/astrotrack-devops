using System.Text;
using AstroTrack.Data;
using AstroTrack.Exceptions;
using AstroTrack.Infrastructure;
using AstroTrack.Repositories.Implementations;
using AstroTrack.Repositories.Interfaces;
using AstroTrack.Services.Implementations;
using AstroTrack.Services.Interfaces;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// ─── Porta (opcional, usada no container) ───────────────────────────────────
var port = builder.Configuration["PORT"];
if (!string.IsNullOrWhiteSpace(port))
    builder.WebHost.UseUrls($"http://+:{port}");

// ─── Controllers ───────────────────────────────────────────────────────────
builder.Services.AddControllers()
    .AddJsonOptions(options =>
    {
        options.JsonSerializerOptions.Converters.Add(
            new System.Text.Json.Serialization.JsonStringEnumConverter());
    });

// ─── HttpContext ────────────────────────────────────────────────────────────
builder.Services.AddHttpContextAccessor();

// ─── Oracle + EF Core ───────────────────────────────────────────────────────
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseOracle(builder.Configuration.GetConnectionString("OracleConnection")));

// ─── Repositories ───────────────────────────────────────────────────────────
builder.Services.AddScoped<IClienteRepository, ClienteRepository>();
builder.Services.AddScoped<IMotoristaRepository, MotoristaRepository>();
builder.Services.AddScoped<IVeiculoRepository, VeiculoRepository>();
builder.Services.AddScoped<IViagemRepository, ViagemRepository>();
builder.Services.AddScoped<ICheckpointRepository, CheckpointRepository>();
builder.Services.AddScoped<IUsuarioRepository, UsuarioRepository>();

// ─── Services ───────────────────────────────────────────────────────────────
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IClienteService, ClienteService>();
builder.Services.AddScoped<IMotoristaService, MotoristaService>();
builder.Services.AddScoped<IVeiculoService, VeiculoService>();
builder.Services.AddScoped<IViagemService, ViagemService>();
builder.Services.AddScoped<ICheckpointService, CheckpointService>();

// ─── Infrastructure ─────────────────────────────────────────────────────────
builder.Services.AddScoped<JwtService>();
builder.Services.AddScoped<CurrentUserService>();

// ─── JWT Authentication ─────────────────────────────────────────────────────
var jwtKey = builder.Configuration["Jwt:Key"]
    ?? throw new InvalidOperationException("JWT key não configurada");

builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidateIssuerSigningKey = true,
        ValidIssuer = builder.Configuration["Jwt:Issuer"],
        ValidAudience = builder.Configuration["Jwt:Audience"],
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtKey))
    };
});

builder.Services.AddAuthorization();

// ─── CORS ────────────────────────────────────────────────────────────────────
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
        policy.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

// ─── Swagger ─────────────────────────────────────────────────────────────────
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "AstroTrack API",
        Version = "v1",
        Description = "API de rastreamento de frotas via satélite para zonas sem sinal - FIAP Global Solution 2026/1",
        Contact = new OpenApiContact { Name = "AstroTrack Team" }
    });

    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.ApiKey,
        Scheme = "Bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Insira o token JWT no formato: Bearer {seu_token}"
    });

    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });

    var xmlFile = $"{System.Reflection.Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    if (File.Exists(xmlPath))
        options.IncludeXmlComments(xmlPath);
});

var app = builder.Build();

// ─── Migrations automáticas (opt-in via RUN_MIGRATIONS=true) ────────────────
if (string.Equals(app.Configuration["RUN_MIGRATIONS"], "true", StringComparison.OrdinalIgnoreCase))
{
    // O Oracle pode demorar para aceitar conexões mesmo após o container subir
    const int maxTentativas = 10;
    var intervalo = TimeSpan.FromSeconds(10);

    for (var tentativa = 1; ; tentativa++)
    {
        try
        {
            using var scope = app.Services.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.Migrate();
            app.Logger.LogInformation("Migrations aplicadas com sucesso");
            break;
        }
        catch (Exception ex) when (tentativa < maxTentativas)
        {
            app.Logger.LogWarning(ex,
                "Falha ao aplicar migrations (tentativa {Tentativa}/{MaxTentativas}). Nova tentativa em {Segundos}s",
                tentativa, maxTentativas, intervalo.TotalSeconds);
            Thread.Sleep(intervalo);
        }
    }
}

// ─── Middleware Pipeline ─────────────────────────────────────────────────────
app.UseMiddleware<GlobalExceptionMiddleware>();

app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "AstroTrack API v1");
    c.RoutePrefix = string.Empty;
});

app.UseCors("AllowAll");
app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

app.Run();

public partial class Program { }