package InputValidation;

import java.util.Scanner;

public class InputHandler implements IInputHandler {
    private Scanner scanner;

    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String getStringInput(String prompt, IInputValidator<String> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            if (validator.isValid(input)) {
                return input;
            } else {
                System.out.println(validator.getErrorMessage());
            }
        }
    }

    @Override
    public int getIntegerInput(String prompt, IInputValidator<Integer> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                if (validator.isValid(value)) {
                    return value;
                } else {
                    System.out.println(validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    @Override
    public double getDoubleInput(String prompt, IInputValidator<Double> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (validator.isValid(value)) {
                    return value;
                } else {
                    System.out.println(validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
}

