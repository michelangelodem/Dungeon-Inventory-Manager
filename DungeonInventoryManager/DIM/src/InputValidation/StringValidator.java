package InputValidation;

public class StringValidator implements IInputValidator {
    private String input;
    private String errorMessage;
    private int maxLength;

    @Override
    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String getInput() {
        return input;
    }

    public StringValidator(String input, String errorMessage, int maxLength) {
        this.input = input;
        this.errorMessage = "";
        this.maxLength = maxLength;
    }

    @Override
    public boolean isValid() {
        System.out.println("Validating input: " + input);
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
