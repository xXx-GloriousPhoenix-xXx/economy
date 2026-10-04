import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public record WeatherRequest(double latitude, double longitude) {
        public WeatherRequest {
            if (latitude < -90.0 || latitude > 90.0) {
                throw new IllegalArgumentException("Широта має бути в діапазоні [-90; 90]");
            }
            if (longitude < -180.0 || longitude > 180.0) {
                throw new IllegalArgumentException("Довгота має бути в діапазоні [-180; 180]");
            }
        }
    }

    public record WeatherResponse(
            double latitude,
            double longitude,
            double temperature,
            double windSpeed
    ) {}

    public static WeatherResponse getForecast(WeatherRequest request) {
        try {
            String url = String.format(
                    Locale.US,
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,wind_speed_10m",
                    request.latitude(), request.longitude()
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "WeatherApp/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Помилка API: HTTP " + response.statusCode());
                return null;
            }

            String json = response.body();

            return new WeatherResponse(
                    extractDouble(json, "\"latitude\":([0-9.-]+)"),
                    extractDouble(json, "\"longitude\":([0-9.-]+)"),
                    extractDouble(json, "\"temperature_2m\":([0-9.-]+)"),
                    extractDouble(json, "\"wind_speed_10m\":([0-9.-]+)")
            );

        } catch (Exception e) {
            System.out.println("Помилка мережі: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        double lat = readCoordinate(scanner, "широту (latitude)", -90.0, 90.0);
        double lon = readCoordinate(scanner, "довготу (longitude)", -180.0, 180.0);

        var request = new WeatherRequest(lat, lon);
        var response = getForecast(request);

        if (response != null) {
            System.out.printf("Координати : %.2f, %.2f%n", response.latitude(), response.longitude());
            System.out.printf("Температура : %.1f °C%n", response.temperature());
            System.out.printf("Вітер : %.1f км/год%n", response.windSpeed());
        }
    }

    private static double readCoordinate(Scanner scanner, String name, double min, double max) {
        while (true) {
            System.out.printf("Введіть %s [%.0f ... %.0f]: ", name, min, max);
            String input = scanner.nextLine().trim().replace(',', '.');

            try {
                double val = Double.parseDouble(input);
                if (val < min || val > max) {
                    System.out.printf("Значення виходить за межі! Має бути від %.0f до %.0f.%n", min, max);
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Некоректне число. Спробуйте ще раз (наприклад, 50.45).");
            }
        }
    }

    private static double extractDouble(String source, String regex) {
        Matcher m = Pattern.compile(regex).matcher(source);
        return m.find() ? Double.parseDouble(m.group(1)) : 0.0;
    }
}