using Weather.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddHttpClient<WeatherExternalService>();

var app = builder.Build();

app.MapControllers();

app.Run();