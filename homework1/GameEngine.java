public class GameEngine {

    private final ConsoleRenderer renderer = new ConsoleRenderer();
    private final InputHandler input = new InputHandler();

    public void startGame() {
        String category = WordDictionary.randomCategory();
        Difficulty difficulty = Difficulty.random();
        var word = WordDictionary.randomWord(category);

        GameState state = new GameState(
                word.value,
                word.hint,
                difficulty.getMaxAttempts()
        );

        while (true) {
            renderer.render(state, category, difficulty);

            if (state.isWon()) {
                renderer.showMessage("Победа! Слово: " + state.getSecretWord());
                return;
            }

            if (state.isLost()) {
                renderer.showMessage("Поражение. Слово было: " + state.getSecretWord());
                return;
            }

            renderer.showMessage("Введите букву или ! для подсказки:");
            String userInput = input.readInput();

            if (userInput.equals("!")) {
                renderer.showMessage("\nПодсказка: " + state.getHint());
                continue;
            }

            if (!input.isValidLetter(userInput)) {
                renderer.showMessage("\nОшибка ввода! Введите одну букву.");
                continue;
            }

            state.processGuess(userInput.charAt(0));
        }
    }
}
