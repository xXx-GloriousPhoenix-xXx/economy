import json
import urllib.request
import urllib.error
from dataclasses import dataclass

@dataclass
class WeatherRequest:
    latitude: float
    longitude: float

    def __post_init__(self):
        if not (-90.0 <= self.latitude <= 90.0):
            raise ValueError("Широта має бути в діапазоні [-90; 90]")
        if not (-180.0 <= self.longitude <= 180.0):
            raise ValueError("Довгота має бути в діапазоні [-180; 180]")


@dataclass
class WeatherResponse:
    latitude: float
    longitude: float
    temperature: float
    wind_speed: float

    def __str__(self):
        return (
            "\nРЕЗУЛЬТАТ \n"
            f"Координати: {self.latitude:.2f}, {self.longitude:.2f}\n"
            f"Температура : {self.temperature:.1f} °C\n"
            f"Вітер : {self.wind_speed:.1f} км/год\n"
        )

def get_forecast(request: WeatherRequest) -> WeatherResponse | None:
    url = (
        f"https://api.open-meteo.com/v1/forecast"
        f"?latitude={request.latitude}&longitude={request.longitude}"
        f"&current=temperature_2m,wind_speed_10m"
    )

    req = urllib.request.Request(url, headers={"User-Agent": "WeatherApp/1.0"})

    try:
        with urllib.request.urlopen(req) as resp:
            data = json.loads(resp.read().decode("utf-8"))

        current = data.get("current", {})
        return WeatherResponse(
            latitude=data.get("latitude", request.latitude),
            longitude=data.get("longitude", request.longitude),
            temperature=current.get("temperature_2m", 0.0),
            wind_speed=current.get("wind_speed_10m", 0.0),
        )
    except urllib.error.HTTPError as e:
        print(f"Помилка API: HTTP {e.code}")
    except Exception as e:
        print(f"Помилка з'єднання: {e}")
    return None


def read_coordinate(prompt: str, min_val: float, max_val: float) -> float:
    while True:
        raw_val = input(f"Введіть {prompt} [{min_val:.0f} ... {max_val:.0f}]: ").strip().replace(",", ".")
        try:
            val = float(raw_val)
            if not (min_val <= val <= max_val):
                print(f"Значення поза межами! Потрібно від {min_val:.0f} до {max_val:.0f}.")
                continue
            return val
        except ValueError:
            print("Некоректне число. Спробуйте ще раз (наприклад, 50.45).")


def main():
    lat = read_coordinate("широту (latitude)", -90.0, 90.0)
    lon = read_coordinate("довготу (longitude)", -180.0, 180.0)

    request_dto = WeatherRequest(latitude=lat, longitude=lon)
    response_dto = get_forecast(request_dto)

    if response_dto:
        print(response_dto)


if __name__ == "__main__":
    main()