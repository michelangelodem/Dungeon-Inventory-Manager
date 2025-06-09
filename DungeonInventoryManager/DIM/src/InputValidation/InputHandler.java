package InputValidation;

import java.util.Scanner;
import InputValidation.IInputValidator;

public class InputHandler {
    private Scanner scanner = new Scanner(System.in);

    public String getValidatedInput(String prompt, IInputValidator validator) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();

            if (validator.isValid()) {
                return input.trim();
            } else {
                System.out.println("Invalid input: " + validator.getErrorMessage());
            }
        }
    }
}

