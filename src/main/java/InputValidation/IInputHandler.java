package InputValidation;

public interface IInputHandler {
    String getStringInput(String prompt, IInputValidator<String> validator);
    int getIntegerInput(String prompt, IInputValidator<Integer> validator);
    double getDoubleInput(String prompt, IInputValidator<Double> validator);
}

