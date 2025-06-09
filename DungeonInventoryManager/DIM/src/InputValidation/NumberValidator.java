package InputValidation;

public class NumberValidator implements IInputValidator {
    private String input;

    public NumberValidator(String input) {
        this.input = input;
    }

    private boolean isInt() {
        try {
            Integer.parseInt(input);
            return true; 
        } catch (RuntimeException e) {
            return false; 
        }
    }

        private boolean isDouble() {
        try {
            Double.parseDouble(input);
            return true; 
        } catch (RuntimeException e) {
            return false; 
        }
    }

    @Override
    public boolean isValid() {
        if (input == null || input.isEmpty()) {
            return false;
        }
        return isInt() || isDouble();
    }

    @Override
    public String getErrorMessage() {
        return "Invalid input. Enter a positive number:";
    }
    
}
