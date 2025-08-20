package InputValidation;

import java.util.Optional;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.application.Platform;

public class GUIInputHandler extends InputHandler {
    
    public GUIInputHandler() {
        super(); // Call the GUI constructor
    }

    // Override methods to ensure they work properly in GUI context
    @Override
    public String getStringInput(String prompt, IInputValidator<String> validator) {
        return getStringInputWithRetry(prompt, validator);
    }

    @Override
    public int getIntegerInput(String prompt, IInputValidator<Integer> validator) {
        return getIntegerInputWithRetry(prompt, validator);
    }

    @Override
    public double getDoubleInput(String prompt, IInputValidator<Double> validator) {
        return getDoubleInputWithRetry(prompt, validator);
    }

    // Enhanced GUI input methods with better error handling
    private String getStringInputWithRetry(String prompt, IInputValidator<String> validator) {
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", false);
            if (!result.isPresent()) {
                return ""; // Return empty string instead of null for better handling
            }
            
            String input = result.get();
            if (validator.isValid(input)) {
                return input;
            } else {
                // Show error and ask if user wants to try again
                if (!showRetryDialog("Invalid Input", validator.getErrorMessage())) {
                    return ""; // User chose not to retry
                }
            }
        }
    }

    private int getIntegerInputWithRetry(String prompt, IInputValidator<Integer> validator) {
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", true);
            if (!result.isPresent()) {
                return 0; // Return 0 instead of -1 for better handling
            }
            
            try {
                int value = Integer.parseInt(result.get());
                if (validator.isValid(value)) {
                    return value;
                } else {
                    if (!showRetryDialog("Invalid Input", validator.getErrorMessage())) {
                        return 0;
                    }
                }
            } catch (NumberFormatException e) {
                if (!showRetryDialog("Invalid Input", "Please enter a valid number.")) {
                    return 0;
                }
            }
        }
    }

    private double getDoubleInputWithRetry(String prompt, IInputValidator<Double> validator) {
        while (true) {
            Optional<String> result = showInputDialog(prompt, "", true);
            if (!result.isPresent()) {
                return 0.0; // Return 0.0 instead of -1.0 for better handling
            }
            
            try {
                double value = Double.parseDouble(result.get());
                if (validator.isValid(value)) {
                    return value;
                } else {
                    if (!showRetryDialog("Invalid Input", validator.getErrorMessage())) {
                        return 0.0;
                    }
                }
            } catch (NumberFormatException e) {
                if (!showRetryDialog("Invalid Input", "Please enter a valid number.")) {
                    return 0.0;
                }
            }
        }
    }

    // Enhanced input dialog with better styling
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
        textField.setPrefWidth(200);
        
        if (defaultValue != null && !defaultValue.isEmpty()) {
            textField.setText(defaultValue);
        }

        // Add number-only filtering if requested
        if (numbersOnly) {
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                // Allow integers and decimals
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

        // Focus on text field and select all text if there's a default value
        Platform.runLater(() -> {
            textField.requestFocus();
            if (defaultValue != null && !defaultValue.isEmpty()) {
                textField.selectAll();
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return textField.getText().trim();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // Enhanced error dialog with retry option
    private boolean showRetryDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText("Input Error");
        alert.setContentText(message + "\n\nWould you like to try again?");

        ButtonType retryButton = new ButtonType("Try Again", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.NO);

        alert.getButtonTypes().setAll(retryButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == retryButton;
    }

    // Utility method for simple confirmations
    public boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Utility method for simple information dialogs
    public void showInformationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to show choice dialog (useful for selections)
    public <T> Optional<T> showChoiceDialog(String title, String message, T defaultChoice, T... choices) {
        ChoiceDialog<T> dialog = new ChoiceDialog<>(defaultChoice, choices);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        
        return dialog.showAndWait();
    }
}