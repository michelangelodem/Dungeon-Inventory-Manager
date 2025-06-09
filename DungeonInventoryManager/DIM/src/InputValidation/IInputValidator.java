package InputValidation;

public interface IInputValidator {
    String getInput();
    void setInput(String input);
    boolean isValid();
    String getErrorMessage();
}
