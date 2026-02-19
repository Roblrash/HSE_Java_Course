import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class WordDictionary {

    private WordDictionary() {
        // запрет на создание экземпляров
    }

    public static final class Word {
        public final String value;
        public final String hint;

        public Word(String value, String hint) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("Слово не может быть пустым!");
            }
            this.value = value.toLowerCase();
            this.hint = hint;
        }
    }

    private static final Map<String, List<Word>> WORDS_BY_CATEGORY = Map.of(
            "животные", List.of(
                    new Word("слон", "крупное животное с хоботом"),
                    new Word("тигр", "полосатый хищник"),
                    new Word("жираф", "животное с длинной шеей")
            ),
            "фрукты", List.of(
                    new Word("яблоко", "часто падает в сказках"),
                    new Word("банан", "длинный жёлтый фрукт"),
                    new Word("груша", "сладкий зелёный фрукт")
            ),
            "профессии", List.of(
                    new Word("программист", "человек, который пишет код"),
                    new Word("врач", "лечит людей"),
                    new Word("учитель", "обучает студентов")
            )
    );

    private static final Random RANDOM = new Random();

    public static String randomCategory() {
        List<String> categories = new ArrayList<>(WORDS_BY_CATEGORY.keySet());
        return categories.get(RANDOM.nextInt(categories.size()));
    }

    public static Word randomWord(String category) {
        List<Word> words = WORDS_BY_CATEGORY.get(category);

        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Неизвестная категория: " + category);
        }

        return words.get(RANDOM.nextInt(words.size()));
    }
}
