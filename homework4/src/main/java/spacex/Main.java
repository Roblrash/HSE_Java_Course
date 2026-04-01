package spacex;

import spacex.cache.FileStorage;
import spacex.http.SpaceXHttpClient;
import spacex.json.JsonBuilder;
import spacex.json.JsonParser;
import spacex.model.Launch;
import spacex.service.ApiResult;
import spacex.service.SpaceXLaunchService;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String BASE_URL = "https://api.spacexdata.com";
    private static final long CACHE_TTL_MS = 5 * 60 * 1000;

    public static void main(String[] args) {
        SpaceXLaunchService service = new SpaceXLaunchService(
                new SpaceXHttpClient(),
                new JsonParser(),
                new JsonBuilder(),
                new FileStorage("cache"),
                BASE_URL,
                CACHE_TTL_MS
        );

        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> printLaunchList(service.getAllLaunches());
                    case "2" -> printLatestLaunch(service.getLatestLaunch());
                    case "3" -> handleDateSearch(scanner, service);
                    case "4" -> printLaunchList(service.searchBySuccess(true));
                    case "5" -> printLaunchList(service.searchBySuccess(false));
                    case "6" -> {
                        service.clearCache();
                        System.out.println("Кеш очищен.");
                    }
                    case "7" -> {
                        System.out.println("Выход из программы.");
                        return;
                    }
                    default -> System.out.println("Неизвестный пункт меню. Попробуйте снова.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("[!] Ошибка данных: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("[!] Ошибка сети/ввода-вывода: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("[!] Непредвиденная ошибка: " + ex.getMessage());
            }

            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("=== SpaceX Launch Explorer ===");
        System.out.println("1. Показать все запуски");
        System.out.println("2. Показать последний запуск");
        System.out.println("3. Поиск запусков по дате");
        System.out.println("4. Показать только успешные запуски");
        System.out.println("5. Показать только неудачные запуски");
        System.out.println("6. Очистить кеш");
        System.out.println("7. Выход");
        System.out.print("Выберите пункт: ");
    }

    private static void handleDateSearch(Scanner scanner, SpaceXLaunchService service) throws IOException {
        System.out.print("Введите дату начала (YYYY-MM-DD): ");
        String startDate = scanner.nextLine().trim();

        System.out.print("Введите дату конца (YYYY-MM-DD): ");
        String endDate = scanner.nextLine().trim();

        validateDate(startDate);
        validateDate(endDate);

        printLaunchList(service.searchByDate(startDate, endDate));
    }

    private static void validateDate(String date) {
        try {
            LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Некорректная дата: " + date);
        }
    }

    private static void printLaunchList(ApiResult<List<Launch>> result) {
        printCacheWarning(result);
        List<Launch> launches = result.getData();
        if (launches.isEmpty()) {
            System.out.println("Запуски не найдены.");
            return;
        }

        for (Launch launch : launches) {
            String date = launch.getDateUtc() != null && launch.getDateUtc().length() >= 10
                    ? launch.getDateUtc().substring(0, 10)
                    : "unknown";
            String success = Boolean.TRUE.equals(launch.getSuccess()) ? "да" : "нет";
            System.out.printf("#%d  %s | %s | Успех: %s%n", launch.getFlightNumber(), launch.getName(), date, success);
        }
    }

    private static void printLatestLaunch(ApiResult<Launch> result) {
        printCacheWarning(result);
        Launch launch = result.getData();
        String success = Boolean.TRUE.equals(launch.getSuccess()) ? "да" : "нет";
        String details = launch.getDetails() == null ? "нет описания" : launch.getDetails();

        System.out.println("Запуск: " + launch.getName());
        System.out.println("Номер: " + launch.getFlightNumber());
        System.out.println("Дата: " + launch.getDateUtc());
        System.out.println("Успех: " + success);
        System.out.println("Описание: " + details);
    }

    private static void printCacheWarning(ApiResult<?> result) {
        if (!result.isFromCache()) {
            return;
        }

        String savedAt = "unknown";
        if (result.getCachedAt() != null) {
            savedAt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.ofEpochMilli(result.getCachedAt()));
        }

        System.out.printf("[!] Сервер недоступен. Показаны данные из кеша (сохранены %s)%n", savedAt);
    }
}
