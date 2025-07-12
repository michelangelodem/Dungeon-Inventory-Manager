package Commands;

import java.util.List;
import java.util.Optional;
import DnDMechanics.DamageCalculator;
import DnDMechanics.RollD20;
import Inventory.IInventoryService;
import Items.Weapon;
import InputValidation.IInputHandler;
import InputValidation.GUIInputHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

public class MakeAttackCommand implements ICommand {
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;
    private RollD20 rollD20;
    private DamageCalculator damageCalculator;

    // Default threshold to pass for a successful attack
    private int thresholdToPass = 10;

    public MakeAttackCommand(IInventoryService inventoryService, IInputHandler inputHandler) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
        this.rollD20 = new RollD20();
        this.damageCalculator = new DamageCalculator();
    }

    // Alternative constructor for GUI mode
    public MakeAttackCommand(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.inputHandler = new GUIInputHandler();
        this.rollD20 = new RollD20();
        this.damageCalculator = new DamageCalculator();
    }

    @Override
    public void execute() {
        List<Weapon> weapons = inventoryService.getAllWeapons();
        if (weapons.isEmpty()) {
            showError("No weapons in inventory to make an attack.");
            return;
        }

        // Show weapon selection dialog
        Optional<Weapon> selectedWeapon = showWeaponSelectionDialog(weapons);
        if (!selectedWeapon.isPresent()) {
            return; // User cancelled
        }

        Weapon chosenWeapon = selectedWeapon.get();

        // Get threshold from user
        Optional<Integer> threshold = showThresholdDialog();
        if (!threshold.isPresent()) {
            return; // User cancelled
        }

        this.thresholdToPass = threshold.get();

        // Perform the attack
        performAttack(chosenWeapon);
    }

    private Optional<Weapon> showWeaponSelectionDialog(List<Weapon> weapons) {
        Dialog<Weapon> dialog = new Dialog<>();
        dialog.setTitle("Select Weapon");
        dialog.setHeaderText("Choose a weapon for your attack:");

        // Create the weapon selection content
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        ListView<Weapon> weaponList = new ListView<>();
        weaponList.getItems().addAll(weapons);
        weaponList.setPrefHeight(200);

        // Custom cell factory to display weapon details
        weaponList.setCellFactory(listView -> new ListCell<Weapon>() {
            @Override
            protected void updateItem(Weapon weapon, boolean empty) {
                super.updateItem(weapon, empty);
                if (empty || weapon == null) {
                    setText(null);
                } else {
                    setText(weapon.getName() + " (Damage: " + weapon.getDamageRoll() + 
                           ", Weight: " + weapon.getWeight() + " lbs)");
                }
            }
        });

        content.getChildren().addAll(
            new Label("Available Weapons:"),
            weaponList
        );

        dialog.getDialogPane().setContent(content);

        // Add buttons
        ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

        // Get the select button and initially disable it
        Button selectButton = (Button) dialog.getDialogPane().lookupButton(selectButtonType);
        selectButton.setDisable(true);

        // Select first weapon by default and enable button
        if (!weapons.isEmpty()) {
            weaponList.getSelectionModel().select(0);
            selectButton.setDisable(false); // Enable since we have a selection
        }

        // Enable/disable select button based on selection changes
        weaponList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectButton.setDisable(newSelection == null);
        });

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == selectButtonType) {
                return weaponList.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Integer> showThresholdDialog() {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Set Attack Threshold");
        dialog.setHeaderText("Enter the Armor Class (AC) or difficulty threshold:");

        // Create the threshold input content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField thresholdField = new TextField();
        thresholdField.setText("10"); // Default value
        thresholdField.setPromptText("Enter threshold (1-30)");

        // Add some common AC examples
        VBox examples = new VBox(5);
        examples.getChildren().addAll(
            new Label("Common AC Values:"),
            new Label("• Easy Target: 8-10"),
            new Label("• Medium Target: 12-14"),
            new Label("• Hard Target: 15-17"),
            new Label("• Very Hard Target: 18-20")
        );

        grid.add(new Label("AC/Threshold:"), 0, 0);
        grid.add(thresholdField, 1, 0);
        grid.add(examples, 0, 1, 2, 1);

        dialog.getDialogPane().setContent(grid);

        // Add buttons
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Validate input
        Button okButton = (Button) dialog.getDialogPane().lookupButton(okButtonType);
        thresholdField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int value = Integer.parseInt(newValue);
                okButton.setDisable(value < 1 || value > 30);
            } catch (NumberFormatException e) {
                okButton.setDisable(true);
            }
        });

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                try {
                    return Integer.parseInt(thresholdField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void performAttack(Weapon weapon) {
        // Create a custom dialog for the attack roll process
        Dialog<Void> attackDialog = new Dialog<>();
        attackDialog.setTitle("Attack Roll");
        attackDialog.setHeaderText("Making attack with " + weapon.getName());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        // Show weapon info
        Label weaponInfo = new Label("Weapon: " + weapon.getName() + " (Damage: " + weapon.getDamageRoll() + ")");
        weaponInfo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label targetInfo = new Label("Target AC: " + thresholdToPass);
        targetInfo.setStyle("-fx-font-size: 12px;");

        Button rollButton = new Button("Roll Attack!");
        rollButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");

        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setPrefRowCount(6);
        detailsArea.setWrapText(true);
        detailsArea.setVisible(false);

        rollButton.setOnAction(e -> {
            // Get roll type from user first
            Optional<String> rollType = showRollTypeDialog();
            if (!rollType.isPresent()) {
                return; // User cancelled roll type selection
            }
            
            // Perform the attack roll based on selected type
            int attackRoll = performRollBasedOnType(rollType.get());
            
            StringBuilder details = new StringBuilder();
            details.append("=== ATTACK ROLL ===\n");
            details.append("Weapon: ").append(weapon.getName()).append("\n");
            details.append("Attack Roll: ").append(attackRoll).append("\n");
            details.append("Target AC: ").append(thresholdToPass).append("\n\n");

            if (attackRoll >= thresholdToPass) {
                // Hit!
                int damage = damageCalculator.calculateDamage(weapon.getDamageRoll());
                
                resultLabel.setText("*** HIT! ***");
                resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #28a745;");
                
                details.append("RESULT: HIT!\n");
                details.append("Damage Roll: ").append(weapon.getDamageRoll()).append("\n");
                details.append("Damage Dealt: ").append(damage).append("\n");
                details.append("\nYou successfully hit with your ").append(weapon.getName())
                       .append(" dealing ").append(damage).append(" damage!");
            } else {
                // Miss!
                resultLabel.setText("*** MISS! ***");
                resultLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
                
                details.append("RESULT: MISS!\n");
                details.append("Your attack fails to hit the target.\n");
                details.append("No damage dealt.");
            }

            detailsArea.setText(details.toString());
            detailsArea.setVisible(true);
            rollButton.setVisible(false);
        });

        content.getChildren().addAll(weaponInfo, targetInfo, rollButton, resultLabel, detailsArea);

        attackDialog.getDialogPane().setContent(content);
        attackDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        attackDialog.showAndWait();
    }

    private Optional<String> showRollTypeDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Roll Type");
        dialog.setHeaderText("Choose your roll type:");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        ToggleGroup rollTypeGroup = new ToggleGroup();
        
        RadioButton normalRoll = new RadioButton("Normal Roll");
        normalRoll.setToggleGroup(rollTypeGroup);
        normalRoll.setSelected(true); // Default selection
        
        RadioButton advantageRoll = new RadioButton("Roll with Advantage (roll twice, take higher)");
        advantageRoll.setToggleGroup(rollTypeGroup);
        
        RadioButton disadvantageRoll = new RadioButton("Roll with Disadvantage (roll twice, take lower)");
        disadvantageRoll.setToggleGroup(rollTypeGroup);

        content.getChildren().addAll(
            new Label("Select roll type:"),
            normalRoll,
            advantageRoll,
            disadvantageRoll
        );

        dialog.getDialogPane().setContent(content);

        ButtonType okButtonType = new ButtonType("Roll", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                RadioButton selected = (RadioButton) rollTypeGroup.getSelectedToggle();
                if (selected == normalRoll) return "normal";
                if (selected == advantageRoll) return "advantage";
                if (selected == disadvantageRoll) return "disadvantage";
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private int performRollBasedOnType(String rollType) {
        switch (rollType) {
            case "advantage":
                return rollD20.roll_diceWithAdvantage();
            case "disadvantage":
                return rollD20.rollWithDisadvantage();
            case "normal":
            default:
                return rollD20.roll_dice();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}