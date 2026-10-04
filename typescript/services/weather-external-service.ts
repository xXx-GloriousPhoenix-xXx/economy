import { OpenMeteoResponse } from "../data/open-meteo-response";

export class WeatherExternalService {
    async getWeatherData(latitude: number, longitude: number): Promise<OpenMeteoResponse | null> {
        const url = `https://api.open-meteo.com/v1/forecast?latitude=${latitude}&longitude=${longitude}&current=temperature_2m,wind_speed_10m`;
        try {
            const response = await fetch(url);
            if (!response.ok) return null;
            
            const data = await response.json();
            return Array.isArray(data) ? data[0] : data;
        } catch (error) {
            console.error('Failed to fetch external weather data:', error);
            return null;
        }
    }
}