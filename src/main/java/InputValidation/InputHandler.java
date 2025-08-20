package InputValidation;

import java.util.Scanner;
import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.application.Platform;

public class InputHandler implements IInputHandler {
    private Scanner scanner;
    private boolean isGUIMode;

    // Constructor for CLI mode
    public InputHandler(Scanner scanner) {
        this.scanner = scanner;
        this.isGUIMode = false;
    }

    // Constructor for GUI mode
    public InputHandler() {
        this.scanner = null;
        this.isGUIMode = true;
    }

    // Method to check if we're in GUI mode
    public boolean isGUIMode() {
        return isGUIMode;
    }

    // Method to switch modes if needed
    public void setGUIMode(boolean guiMode) {
        this.isGUIMode = guiMode;
    }

    @Override
    public String getStringInput(String prompt, IInputValidator<String> validator) {
        if (isGUIMode) {
            return getStringInputGUI(prompt, validator);
        } else {
            return getStringInputCLI(prompt, validator);
        }
    }

    @Override
    public int getIntegerInput(String prompt, IInputValidator<Integer> validator) {
        if (isGUIMode) {
            return getIntegerInputGUI(prompt, validator);
        } else {
            return getIntegerInputCLI(prompt, validator);
        }
    }

    @Override
    public double getDoubleInput(String prompt, IInputValidator<Double> validator) {
        if (isGUIMode) {
            return getDoubleInputGUI(prompt, validator);
        } else {
            return getDoubleInputCLI(prompt, validator);
        }
    }

    // CLI Implementation Methods
    private String getStringInputCLI(String prompt, IInputValidator<String> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            if (validator.isValid(input)) {
                return input;
            } else {
                System.out.println(validator.getErrorMessage());
            }
        }
    }

    private int getIntegerInputCLI(String prompt, IInputValidator<Integer> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                if (validator.isValid(value)) {
                    return value;
                } else {
                    System.out.println(validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private double getDoubleInputCLI(String prompt, IInputValidator<Double> validator) {
        String input;
        while (true) {
            System.out.println(prompt);
            input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (validator.isValid(value)) {
                    return value;
                } else {
                    System.out.println(validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    // GUI Implementation Methods
    private String getStringInputGUI(String prompt, IInputValidator<String> validator) {
        String input;
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", false);
            if (!result.isPresent()) {
                return null; // User cancelled
            }
            
            input = result.get();
            if (validator.isValid(input)) {
                return input;
            } else {
                showErrorDialog("Invalid Input", validator.getErrorMessage());
            }
        }
    }

    private int getIntegerInputGUI(String prompt, IInputValidator<Integer> validator) {
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", true);
            if (!result.isPresent()) {
                return -1; // User cancelled - you might want to handle this differently
            }
            
            try {
                int value = Integer.parseInt(result.get());
                if (validator.isValid(value)) {
                    return value;
                } else {
                    showErrorDialog("Invalid Input", validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter a valid number.");
            }
        }
    }

    private double getDoubleInputGUI(String prompt, IInputValidator<Double> validator) {
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", true);
            if (!result.isPresent()) {
                return -1.0; // User cancelled - you might want to handle this differently
            }
            
            try {
                double value = Double.parseDouble(result.get());
                if (validator.isValid(value)) {
                    return value;
                } else {
                    showErrorDialog("Invalid Input", validator.getErrorMessage());
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid Input", "Please enter a valid number.");
            }
        }
    }

    // Helper Methods for GUI
    private Optional<String> showInputDialog(String prompt, String defaultValue, boolean numbersOnly) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(prompt);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField textField = new TextField();
        textField.setPromptText("Enter value here...");
        if (defaultValue != null && !defaultValue.isEmpty()) {
            textField.setText(defaultValue);
        }

        // Add number-only filtering if requested
        if (numbersOnly) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*\\.?\\d*")) {
                    textField.setText(oldValue);
                }
            });
        }

        grid.add(new Label("Input:"), 0, 0);
        grid.add(textField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Enable/disable OK button based on input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Focus on text field
        Platform.runLater(() -> textField.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Utility method to create appropriate InputHandler based on context
    public static InputHandler createForGUI() {
        return new InputHandler();
    }

    public static InputHandler createForCLI(Scanner scanner) {
        return new InputHandler(scanner);
    }
}