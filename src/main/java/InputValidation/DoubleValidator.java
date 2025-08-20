package InputValidation;

public class DoubleValidator implements IInputValidator<Double> {
    private double min;
    private double max;

    public DoubleValidator(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isValid(Double input) {
        return input != null && input >= min && input <= max;
    }

    @Override
    public String getErrorMessage() {
        return "Input must be a number between " + min + " and " + max + ".";
    }
}

