namespace Weather.Services;

public class WeatherExternalService(HttpClient httpClient)
{
    public async Task<List<WeatherApiResponse>?> GetWeatherDataAsync(double latitude, double longitude)
    {
        var url = $"https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m,wind_speed_10m";
        return await httpClient.GetFromJsonAsync<List<WeatherApiResponse>>(url);
    }
}