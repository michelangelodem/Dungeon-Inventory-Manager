package Commands;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import DnDMechanics.DamageCalculator;
import DnDMechanics.RollD20;
import Inventory.IInventoryService;
import Items.Weapon;
import InputValidation.IInputHandler;
import InputValidation.IInputValidator;
import InputValidation.GUIInputHandler;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import View.SetGrid;

public class MakeAttackCommand implements ICommand {
    IInputValidator<Integer> intValidator = new IInputValidator<Integer>() {
        @Override
        public boolean isValid(Integer value) {
            return value != null && value >= 1 && value <= 30; // or your logic
        }
        @Override
        public String getErrorMessage() {
            return "Please enter a valid integer between 1 and 30.";
        }
    };
    private IInputHandler inputHandler;
    private IInventoryService inventoryService;
    // Using DnD mechanics classes for rolling and damage calculation
    private RollD20 rollD20;
    private DamageCalculator damageCalculator;

    // Default threshold to pass for a successful attack
    private int thresholdToPass = 10;
    // Modifier for the attack roll, can be set by user input
    private int modifier = 0;

    // Constructor for console mode
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

        // Use SetGrid for weapon selection
        SetGrid<Weapon> weaponGrid = new SetGrid<>(
            "Select Weapon",
            "Choose a weapon for your attack:",
            null,
            null,
            new GUIInputHandler(),
            null,
            null
        );
        Optional<Weapon> selectedWeapon = weaponGrid.showSelectionDialog(weapons, "Choose a weapon for your attack:");
        if (!selectedWeapon.isPresent()) {
            return; // User cancelled
        }
        Weapon chosenWeapon = selectedWeapon.get();

        // Use SetGrid for threshold input
        SetGrid<Integer> thresholdGrid = new SetGrid<>(
            "Set Attack Threshold",
            "Enter the Armor Class (AC) or difficulty threshold:",
            "10",
            "AC/Threshold",
            new GUIInputHandler(),
            intValidator,
            Integer::parseInt
        );
        Optional<Integer> threshold = thresholdGrid.showDialog();
        if (!threshold.isPresent()) {
            return; // User cancelled
        }

        // Use SetGrid for attack modifier input
        SetGrid<Integer> modifierGrid = new SetGrid<>(
            "Set Attack Modifier",
            "Enter the attack modifier for your roll (e.g., +2, -1)",
            "0",
            "Attack Modifier",
            new GUIInputHandler(),
            intValidator,
            Integer::parseInt
        );
        Optional<Integer> attackModifier = modifierGrid.showDialog();
        if (!attackModifier.isPresent()) {
            return; // User cancelled
        }

        this.modifier = attackModifier.get();
        this.thresholdToPass = threshold.get();

        // Perform the attack
        performAttack(chosenWeapon);
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
            int attackRoll = performRollBasedOnType(rollType.get()) + modifier;
            
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