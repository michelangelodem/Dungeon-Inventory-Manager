package InputValidation;

public class DamageValidator implements IInputValidator<String> {
    private static final String DAMAGE_REGEX = "^\\d+d\\d+(?:\\s*[+\\-]\\s*\\d+)?$";

    @Override
    public boolean isValid(String input) {
        return input != null && input.matches(DAMAGE_REGEX);
    }

    @Override
    public String getErrorMessage() {
        return "Invalid damage format. Please use 'xdy' or 'xdy + z' (e.g., '2d6', '1d8 + 2').";
    }
}

