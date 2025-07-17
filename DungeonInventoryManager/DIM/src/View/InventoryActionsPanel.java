package View;

import Commands.*;
import Inventory.InventoryService;
import Items.ItemFactory;
import Items.Item;
import Items.Weapon;
import InputValidation.GUIInputHandler;
import InputValidation.IInputValidator;
import InputValidation.IInputValidator;
import FileManagement.FileService;
import User.User;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Optional;

public class InventoryActionsPanel {
    private IInputValidator<Integer> validator;
    private VBox actionsPanel;
    private InventoryService inventoryService;
    private GUIInputHandler inputHandler;
    private ItemFactory itemFactory;
    private FileService fileService;
    private User currentUser;
    private Runnable refreshCallback;
    
    public InventoryActionsPanel(InventoryService inventoryService, User currentUser, Runnable refreshCallback) {
        this.inventoryService = inventoryService;
        this.currentUser = currentUser;
        this.refreshCallback = refreshCallback;
        this.inputHandler = new GUIInputHandler();
        this.itemFactory = new ItemFactory();
        this.fileService = new FileService();
        
        createActionsPanel();
    }
    
    private void createActionsPanel() {
        actionsPanel = new VBox(10);
        actionsPanel.setPadding(new Insets(15));
        actionsPanel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1px; -fx-border-radius: 5px;");
        
        Label titleLabel = new Label("Inventory Actions");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        
        // Create action buttons
        Button addItemBtn = createActionButton("Add Item", "#28a745");
        Button removeItemBtn = createActionButton("Remove Item", "#dc3545");
        Button searchItemBtn = createActionButton("Search Items", "#17a2b8");
        Button makeAttackBtn = createActionButton("Make Attack", "#fd7e14");
        Button exportBtn = createActionButton("Export Inventory", "#6c757d");
        Button saveBtn = createActionButton("Save Inventory", "#007bff");
        
        // Set button actions
        addItemBtn.setOnAction(e -> executeAddItemCommand());
        removeItemBtn.setOnAction(e -> executeRemoveItemCommand());
        searchItemBtn.setOnAction(e -> executeSearchItemCommand());
        makeAttackBtn.setOnAction(e -> executeMakeAttackCommand());
        exportBtn.setOnAction(e -> executeExportCommand());
        saveBtn.setOnAction(e -> executeSaveCommand());
        
        // Add all elements to the panel
        actionsPanel.getChildren().addAll(
            titleLabel,
            new Separator(),
            addItemBtn,
            removeItemBtn,
            searchItemBtn,
            makeAttackBtn,
            new Separator(),
            exportBtn,
            saveBtn
        );
    }
    
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 12px; " +
            "-fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-background-radius: 4px;",
            color
        ));
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            String darkerColor = getDarkerColor(color);
            button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-background-radius: 4px;",
                darkerColor
            ));
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-background-radius: 4px;",
                color
            ));
        });
        
        return button;
    }
    
    private String getDarkerColor(String color) {
        switch (color) {
            case "#28a745": return "#1e7e34";
            case "#dc3545": return "#bd2130";
            case "#17a2b8": return "#117a8b";
            case "#fd7e14": return "#e8680a";
            case "#6c757d": return "#545b62";
            case "#007bff": return "#0056b3";
            default: return color;
        }
    }
    
    private void executeAddItemCommand() {
        AddItemCommand command = new AddItemCommand(inventoryService, inputHandler, itemFactory);
        command.execute();
        refreshCallback.run();
        showSuccess("Item added successfully!");
    }
    
    private void executeRemoveItemCommand() {
        List<Item> items = inventoryService.getAllItems();
        if (items.isEmpty()) {
            showError("No items in inventory to remove.");
            return;
        }

        // When you create a ChoiceDialog<Item>, 
        // JavaFX uses the toString() method of the Item objects to display them in the dropdown.
        // This made the dialog show the Item Object's Class and HashCode instead of the item name.
        // To fix this, we need to extract the item names and use them in the ChoiceDialog.

        List<String> itemNames = items.stream()
            .map(Item::getName)
            .collect(java.util.stream.Collectors.toList());
        
        // Create a choice dialog with all items
        ChoiceDialog<String> dialog = new ChoiceDialog<>(itemNames.get(0), itemNames);
        dialog.setTitle("Remove Item");
        dialog.setHeaderText("Select item to remove:");
        dialog.setContentText("Item:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            inventoryService.removeItem(result.get());
            refreshCallback.run();
            showSuccess("Item removed successfully!");
        }
    }
    
    private void executeSearchItemCommand() {
        SearchItemsCommand command = new SearchItemsCommand(inventoryService, inputHandler);
        command.execute();
    }
    
    private void executeMakeAttackCommand() {
        List<Weapon> weapons = inventoryService.getAllWeapons();
        if (weapons.isEmpty()) {
            showError("No weapons in inventory to make an attack.");
            return;
        }
        
        MakeAttackCommand command = new MakeAttackCommand(inventoryService);
        command.execute();
    }
    
    private void executeExportCommand() {
        // Create a custom export command that uses user-specific filename
        ExportInventoryCommand command = new ExportInventoryCommand(inventoryService) {
            @Override
            public void execute() {
                String fileName = "inventory_" + currentUser.getUsername() + ".txt";
                prepareFileForWrite(fileName);
                List<Item> items = inventoryService.getAllItems();
                for (Item item : items) {
                    writeItemToFile(item, fileName);
                }
                showSuccess("Items exported to: " + fileName);
            }
            
            private void prepareFileForWrite(String fileName) {
                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName, false))) {
                    writer.write("");
                } catch (java.io.IOException e) {
                    showError("Error preparing file for writing: " + e.getMessage());
                }
            }
            
            private void writeItemToFile(Item item, String fileName) {
                try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName, true))) {   
                    writer.write("--- " + item.getClass().getSimpleName() + " ---\n"); 
                    item.writeToStream(writer); 
                    writer.newLine();
                } catch (java.io.IOException e) {
                    showError("Error writing to file: " + e.getMessage());
                }
            }
        };
        
        command.execute();
    }
    
    private void executeSaveCommand() {
        String userInventoryFile = currentUser.getInventoryFileName();
        fileService.writeItemsToFile(inventoryService.getAllItems(), userInventoryFile);
        showSuccess("Inventory saved successfully!");
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public VBox getActionsPanel() {
        return actionsPanel;
    }
}
