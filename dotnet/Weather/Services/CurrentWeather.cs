using System.Text.Json.Serialization;

namespace Weather.Services;

public class CurrentWeather
{
    [JsonPropertyName("temperature_2m")]
    public double Temperature2M { get; set; }

    [JsonPropertyName("wind_speed_10m")]
    public double WindSpeed10M { get; set; }
}