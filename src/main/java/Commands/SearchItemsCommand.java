package Commands;

import java.util.List;
import Inventory.IInventoryService;
import Items.*;
import InputValidation.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.geometry.Pos;

public class SearchItemsCommand implements ICommand {
    private IInputValidator<String> validator = new StringValidator();
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;

    private final boolean IS_GUI = true; // Flag to determine if GUI is used

    public SearchItemsCommand(IInventoryService inventoryService, IInputHandler inputHandler) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
    }

    public SearchItemsCommand(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
        this.inputHandler = new GUIInputHandler();
    }

    @Override
    public void execute() {
        String searchTerm = inputHandler.getStringInput("Enter item name to search:", validator);
        List<Item> foundItems = inventoryService.searchItem(searchTerm);
        
        if (!IS_GUI) {
            if (foundItems.isEmpty()) {
                System.out.println("No items found matching \'" + searchTerm + "\'.");
            } else {
                System.out.println("Found items:");
                foundItems.forEach(Item::display);
            }
        } else if (IS_GUI) {
            if (foundItems.isEmpty()) {
                showError(searchTerm);
            } else {
                showFoundItems(foundItems);
            }
        }
    }

    private void showError(String message) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #afa2a2ff;"); 
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
                
        Label noItemsLabel = new Label("No items found matching \'" + message + "\'.");
        noItemsLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
        grid.add(noItemsLabel, 0, 0);
        grid.setAlignment(Pos.CENTER);

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Result");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    private void showFoundItems(List<Item> foundItems) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: #c5b8eaff;"); 
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        Label foundItemsLabel = new Label("Found items:");
        foundItemsLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        grid.add(foundItemsLabel, 0, 0);
        grid.setAlignment(Pos.CENTER);

        for (Item item : foundItems) {
            Label itemLabel = new Label();
            chooseLabel(item, itemLabel);
            grid.add(itemLabel, 0, grid.getRowCount());
        }
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search Result");
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private void chooseLabel(Item item, Label itemLabel) {
        if (item instanceof Weapon) {
            itemLabel.setText(item.getName() + 
                                        "\nDescription:" + item.getDescription() +
                                        "\nDamage Roll: " + ((Weapon)item).getDamageRoll()                                        
                                        );
        } else if (item instanceof Armor) {
            itemLabel.setText(item.getName() + 
                                        "\nDescription:" + item.getDescription() +
                                        "\nArmor Class: " + ((Armor)item).getDefense()                                        
                                        );
        } else {
            itemLabel.setText(item.getName() + 
                                        "\nDescription:" + item.getDescription()                                        
                                        );
        }

        itemLabel.setWrapText(true);
        itemLabel.setMaxWidth(300); // Limit width for better readability
        itemLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
    }
}

