using mycampus_backend.Repositories;
using mycampus_backend.Services;
using Microsoft.EntityFrameworkCore;
using mycampus_backend.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Register custom services
//builder.Services.AddScoped<ITestRepository, TestRepository>();
//builder.Services.AddScoped<ITestService, TestService>();

var connectionstring =
builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));
Console.WriteLine($"Connection String: {connectionstring}");

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();
