package InputValidation;

public class ItemTypeValidator implements IInputValidator<String> {
    @Override
    public boolean isValid(String input) {
        return input != null && (input.equalsIgnoreCase("Weapon") || input.equalsIgnoreCase("Armor") || input.equalsIgnoreCase("Regular"));
    }

    @Override
    public String getErrorMessage() {
        return "Invalid item type. Please enter Weapon, Armor, or Regular.";
    }
}

