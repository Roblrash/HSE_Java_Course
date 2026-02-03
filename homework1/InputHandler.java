import java.util.Scanner;

public class InputHandler {

    private final Scanner scanner = new Scanner(System.in);

    public String readInput() {
        return scanner.nextLine().trim().toLowerCase();
    }

    public boolean isValidLetter(String input) {
        return input.length() == 1 && Character.isLetter(input.charAt(0));
    }
}
