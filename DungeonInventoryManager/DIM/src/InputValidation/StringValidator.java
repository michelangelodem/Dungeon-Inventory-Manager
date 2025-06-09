package InputValidation;

public class StringValidator implements IInputValidator {
    private String input;
    private String errorMessage;
    private int maxLength;

    public StringValidator(String input, String errorMessage, int maxLength) {
        this.input = input;
        this.errorMessage = "";
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid() {
        if (input == null || input.trim().isEmpty()) {
            errorMessage = "Input cannot be null or empty";
            return false;
        }
        if (input.length() > maxLength) {
            errorMessage = "Input cannot exceed 100 characters";
            return false;
        }
        return true;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
    
}
