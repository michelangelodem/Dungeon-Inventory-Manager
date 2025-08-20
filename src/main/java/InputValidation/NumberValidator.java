package InputValidation;

public class NumberValidator implements IInputValidator<Integer> {
    private int min;
    private int max;

    public NumberValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Integer input) {
        return input != null && input >= min && input <= max;
    }

    @Override
    public String getErrorMessage() {
        return "Input must be a number between " + min + " and " + max + ".";
    }
}

