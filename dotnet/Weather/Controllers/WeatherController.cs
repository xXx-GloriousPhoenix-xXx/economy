using Microsoft.AspNetCore.Mvc;
using Weather.Contracts;
using Weather.Services;

namespace Weather.Controllers;

[ApiController]
[Route("api/weather")]
public class WeatherController(WeatherExternalService weatherService) : ControllerBase
{
    [HttpPost]
    public async Task<ActionResult<WeatherResponseDto>> GetForecast([FromBody] WeatherRequestDto request)
    {
        var apiResponses = await weatherService.GetWeatherDataAsync(request.Latitude, request.Longitude);
        var apiResponse = apiResponses?.FirstOrDefault();

        if (apiResponse?.Current == null)
        {
            return NotFound("Failed to fetch weather data from external API.");
        }

        var response = new WeatherResponseDto
        {
            Latitude = apiResponse.Latitude,
            Longitude = apiResponse.Longitude,
            Temperature = apiResponse.Current.Temperature2M,
            WindSpeed = apiResponse.Current.WindSpeed10M
        };

        return Ok(response);
    }
}