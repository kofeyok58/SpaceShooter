package edu.engine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Простое хранилище рекордов в JSON.
 * Сохраняем ТОЛЬКО лучшие 10 результатов.
 */
public final class HighScores {

    public static final class Entry {
        public String name;
        public int score;
        public String date; // ISO-строка для простоты

        public Entry(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<Entry>>(){}.getType();

    // файл в домашней папке пользователя (не в resources — нужно иметь права на запись)
    private static final Path FILE = Paths.get(
            System.getProperty("user.home"),
            ".spaceshooter_scores.json"
    );

    // загрузка с диска; если нет файла/ошибка — пустой список
    public static List<Entry> load() {
        try {
            if (Files.exists(FILE)) {
                String json = Files.readString(FILE, StandardCharsets.UTF_8);
                List<Entry> list = GSON.fromJson(json, LIST_TYPE);
                if (list != null) {
                    // сортируем по убыванию очков
                    list.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
                    // ограничиваем топ-10
                    if (list.size() > 10) return new ArrayList<>(list.subList(0, 10));
                    return list;
                }
            }
        } catch (Exception ignored) {}
        return new ArrayList<>();
    }

    // сохранить список на диск (перезаписываем)
    private static void save(List<Entry> list) throws IOException {
        String json = GSON.toJson(list, LIST_TYPE);
        Files.writeString(FILE, json, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    // добавить запись и удержать топ-10
    public static void add(String name, int score) {
        List<Entry> list = load();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        list.add(new Entry(name, score, ts));
        list.sort(Comparator.comparingInt((Entry e) -> e.score).reversed());
        if (list.size() > 10) {
            list = new ArrayList<>(list.subList(0, 10));
        }
        try {
            save(list);
        } catch (IOException e) {
            System.err.println("Не удалось сохранить рекорды: " + e.getMessage());
        }
    }

    // получить копию топ-10 (для отображения)
    public static List<Entry> top() {
        return new ArrayList<>(load());
    }

    // (опционально) очистка — можно вызвать из скрытой кнопки
    public static void clear() {
        try {
            save(new ArrayList<>());
        } catch (IOException e) {
            System.err.println("Не удалось очистить рекорды: " + e.getMessage());
        }
    }

    // путь к файлу (вдруг нужно подсветить в UI)
    public static Path filePath() { return FILE; }
}
