package Commands;

import java.util.List;
import Inventory.IInventoryService;
import Items.Item;
import InputValidation.*;

public class SearchItemsCommand implements ICommand {
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;

    public SearchItemsCommand(IInventoryService inventoryService, IInputHandler inputHandler) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
    }

    @Override
    public void execute() {
        String searchTerm = inputHandler.getStringInput("Enter item name to search:", new StringValidator());
        List<Item> foundItems = inventoryService.searchItem(searchTerm);

        if (foundItems.isEmpty()) {
            System.out.println("No items found matching \'" + searchTerm + "\'.");
        } else {
            System.out.println("Found items:");
            foundItems.forEach(Item::display);
        }
    }
}

