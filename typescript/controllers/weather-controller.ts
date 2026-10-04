import { Request, Response } from 'express';
import type { WeatherRequestDto } from '../data/weather-request-dto.js';
import type { WeatherResponseDto } from '../data/weather-response-dto.js';
import { WeatherExternalService } from '../services/weather-external-service.js';

const weatherService = new WeatherExternalService();
export async function getForecast(req: Request<{}, {}, WeatherRequestDto>, res: Response) {
    const { latitude, longitude } = req.body;

    if (latitude === undefined || longitude === undefined) {
        return res.status(400).json({ error: 'Latitude and longitude are required.' });
    }

    const apiResponse = await weatherService.getWeatherData(latitude, longitude);

    if (!apiResponse || !apiResponse.current) {
        return res.status(404).json({ error: 'Failed to fetch weather data from external API.' });
    }

    const responseDto: WeatherResponseDto = {
        latitude: apiResponse.latitude,
        longitude: apiResponse.longitude,
        temperature: apiResponse.current.temperature_2m,
        windSpeed: apiResponse.current.wind_speed_10m
    };

    return res.json(responseDto);
}