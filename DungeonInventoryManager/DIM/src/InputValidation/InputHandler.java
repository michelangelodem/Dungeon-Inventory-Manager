package InputValidation;

public class InputHandler {

    public String getValidatedInput(IInputValidator validator) {
        while (true) {
            if (validator.isValid()) {
                return validator.getInput().trim();
            } else {
                System.out.println("Invalid input: " + validator.getErrorMessage());
            }
        }
    }
}

