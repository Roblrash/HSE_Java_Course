import java.util.HashSet;
import java.util.Set;

public class GameState {

    private final String secretWord;
    private final String hint;
    private final int maxAttempts;

    private final Set<Character> correctGuesses = new HashSet<>();
    private final Set<Character> wrongGuesses = new HashSet<>();

    public GameState(String secretWord, String hint, int maxAttempts) {
        if (secretWord == null || secretWord.isBlank()) {
            throw new IllegalArgumentException("Слово не должно быть пустым!");
        }
        this.secretWord = secretWord.toLowerCase();
        this.hint = hint;
        this.maxAttempts = maxAttempts;
    }

    public boolean processGuess(char letter) {
        letter = Character.toLowerCase(letter);

        if (secretWord.indexOf(letter) >= 0) {
            correctGuesses.add(letter);
            return true;
        } else {
            wrongGuesses.add(letter);
            return false;
        }
    }

    public boolean isWon() {
        for (char c : secretWord.toCharArray()) {
            if (!correctGuesses.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public boolean isLost() {
        return wrongGuesses.size() >= maxAttempts;
    }

    public int getRemainingAttempts() {
        return maxAttempts - wrongGuesses.size();
    }

    public int getErrorCount() {
        return wrongGuesses.size();
    }

    public String getMaskedWord() {
        StringBuilder result = new StringBuilder();
        for (char c : secretWord.toCharArray()) {
            result.append(correctGuesses.contains(c) ? c : '_').append(' ');
        }
        return result.toString();
    }

    public String getWrongGuesses() {
        StringBuilder result = new StringBuilder();
        for (Character c : wrongGuesses) {
            result.append(c).append(' ');
        }
        return result.toString();
    }

    public String getHint() {
        return hint;
    }

    public String getSecretWord() {
        return secretWord;
    }
}
