public class ConsoleRenderer {

    public void render(GameState state, String category, Difficulty difficulty) {
        System.out.println("Категория: " + category);
        System.out.println("Сложность: " + difficulty);
        System.out.println(HangmanArt.STAGES[
                Math.min(state.getErrorCount(), HangmanArt.STAGES.length - 1)
                ]);
        System.out.println("\nСлово: " + state.getMaskedWord());
        System.out.println("\nОшибочные буквы: " + state.getWrongGuesses());
        System.out.println("Осталось попыток: " + state.getRemainingAttempts());
    }

    public void showMessage(String message) {
        System.out.println(message);
    }
}
