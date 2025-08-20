package InputValidation;

public class StringValidator implements IInputValidator<String> {
    @Override
    public boolean isValid(String input) {
        return input != null && !input.trim().isEmpty();
    }

    @Override
    public String getErrorMessage() {
        return "Input cannot be empty.";
    }
}

