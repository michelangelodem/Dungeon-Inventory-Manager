package Commands;

import Inventory.IInventoryService;
import InputValidation.IInputHandler;
import InputValidation.StringValidator;

public class RemoveItemCommand implements ICommand {
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;

    public RemoveItemCommand(IInventoryService inventoryService, IInputHandler inputHandler) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
    }

    @Override
    public void execute() {
        String itemName = inputHandler.getStringInput("Enter the name of the item to remove:", new StringValidator());
        inventoryService.removeItem(itemName);
    }
}

