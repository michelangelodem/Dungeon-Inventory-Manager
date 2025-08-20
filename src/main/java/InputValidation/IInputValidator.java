package InputValidation;

public interface IInputValidator<T> {
    boolean isValid(T input);
    String getErrorMessage();
}

