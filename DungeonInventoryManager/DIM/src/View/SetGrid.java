package View;

import java.util.List;
import java.util.Optional;

//import javax.xml.validation.Validator;

import InputValidation.IInputValidator;
import InputValidation.GUIInputHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.util.function.Function;

import Items.*;

public class SetGrid<T> {
    private IInputValidator<T> validator;
    private GUIInputHandler inputHandler; // Handler for input validation
    private String defaultValue = null; // Default value for the input field
    private String TITLE = null;
    private String HEADER_TEXT = null;
    private String label = null;
    private Function<String, T> parser;
    private GridPane grid;

    public SetGrid(String title, String headerText, String defaultValue, String label, GUIInputHandler inputHandler, IInputValidator<T> validator, Function<String, T> parser) {
        this.TITLE = title;
        this.HEADER_TEXT = headerText;
        this.defaultValue = defaultValue;
        this.label = label;
        this.inputHandler = inputHandler;
        this.validator = validator;
        this.parser = parser;

        // Initialize the grid layout
        grid = new GridPane();
        grid.setStyle("-fx-background-color: #a62525ff;"); // Optional: Set background color
        grid.setGridLinesVisible(true); // Optional: Show grid lines for better visibility
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    }
    
    public Optional<T> showDialog() {
                Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(TITLE);
        dialog.setHeaderText(HEADER_TEXT);

        // Create a new grid and input field for each dialog instance to avoid duplicate nodes
        GridPane localGrid = new GridPane();
        localGrid.setStyle("-fx-background-color: #ffffffff;");
        localGrid.setGridLinesVisible(true);
        localGrid.setHgap(20);
        localGrid.setVgap(20);
        localGrid.setPadding(new Insets(20, 150, 10, 10));

        TextField localInputField = new TextField();
        localInputField.setText(defaultValue);
        localInputField.setPromptText("Enter value");

        localGrid.add(new Label(label), 0, 0);
        localGrid.add(localInputField, 1, 0);

        dialog.getDialogPane().setContent(localGrid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        localInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                T parsedValue = parser.apply(newValue);
                boolean valid = validator != null && validator.isValid(parsedValue);
                okButton.setDisable(!valid);
                if (!valid && !newValue.isEmpty() && !valid) {
                    inputHandler.showInformationDialog("Invalid Input", validator.getErrorMessage());
                }
            } catch (Exception e) {
                okButton.setDisable(true);
                if (!newValue.isEmpty()) {
                    inputHandler.showInformationDialog("Invalid Input", "Please enter a valid value.");
                }
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    T parsedValue = parser.apply(localInputField.getText());
                    if (validator != null && validator.isValid(parsedValue)) {
                        return parsedValue;
                    }
                } catch (Exception e) {
                    // ignore, will return null
                }
            }
            return null;
        
        });

        return dialog.showAndWait();
    }

    public Optional<T> showDialog(T value) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(TITLE);
        dialog.setHeaderText(HEADER_TEXT);

        // Create a new grid and input field for each dialog instance to avoid duplicate nodes
        GridPane localGrid = new GridPane();
        localGrid.setStyle("-fx-background-color: #a62525ff;");
        localGrid.setGridLinesVisible(true);
        localGrid.setHgap(10);
        localGrid.setVgap(10);
        localGrid.setPadding(new Insets(20, 150, 10, 10));

        TextField localInputField = new TextField();
        localInputField.setText(defaultValue);
        localInputField.setPromptText("Enter value");

        localGrid.add(new Label(label), 0, 0);
        localGrid.add(localInputField, 1, 0);

        dialog.getDialogPane().setContent(localGrid);

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);

        localInputField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                T parsedValue = parser.apply(newValue);
                boolean valid = validator != null && validator.isValid(parsedValue);
                okButton.setDisable(!valid);
                if (!valid && !newValue.isEmpty() && !valid) {
                    inputHandler.showInformationDialog("Invalid Input", validator.getErrorMessage());
                }
            } catch (Exception e) {
                okButton.setDisable(true);
                if (!newValue.isEmpty()) {
                    inputHandler.showInformationDialog("Invalid Input", "Please enter a valid value.");
                }
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    T parsedValue = parser.apply(localInputField.getText());
                    if (validator != null && validator.isValid(parsedValue)) {
                        return parsedValue;
                    }
                } catch (Exception e) {
                    // ignore, will return null
                }
            }
            return null;
        
        });

        return dialog.showAndWait();
    }
    
    /**
     * Shows a dialog to select a value from a list of T.
     * @param values The list of values to choose from
     * @param prompt The prompt/header for the dialog
     * @return Optional<T> with the selected value, or empty if cancelled
     */
    public Optional<T> showSelectionDialog(List<T> values, String prompt) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(TITLE);
        dialog.setHeaderText(prompt);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        javafx.scene.control.ListView<T> listView = new javafx.scene.control.ListView<>();
        listView.getItems().addAll(values);
        listView.setPrefHeight(200);


        vbox.getChildren().addAll(new Label("Select an option:"), listView);
        dialog.getDialogPane().setContent(vbox);

        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        Button selectButton = (Button) dialog.getDialogPane().lookupButton(selectButtonType);
        selectButton.setDisable(true);

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectButton.setDisable(newSelection == null);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
