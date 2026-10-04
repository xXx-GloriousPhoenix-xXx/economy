export interface OpenMeteoResponse {
    latitude: number;
    longitude: number;
    current?: {
        temperature_2m: number;
        wind_speed_10m: number;
    };
}