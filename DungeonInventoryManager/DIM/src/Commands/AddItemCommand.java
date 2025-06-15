package Commands;

import Inventory.IInventoryService;
import InputValidation.IInputHandler;
import InputValidation.ItemTypeValidator;
import InputValidation.DoubleValidator;
import InputValidation.StringValidator;
import Items.Item;
import Items.IItemFactory;

public class AddItemCommand implements ICommand {
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;
    private IItemFactory itemFactory;

    public AddItemCommand(IInventoryService inventoryService, IInputHandler inputHandler, IItemFactory itemFactory) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
        this.itemFactory = itemFactory;
    }

    @Override
    public void execute() {
        String itemType = inputHandler.getStringInput("Enter item type (Weapon/Armor/Regular):", new ItemTypeValidator());
        String name = inputHandler.getStringInput("Enter item name:", new StringValidator());
        String description = inputHandler.getStringInput("Enter item description (appearance and capabilities):", new StringValidator());
        double price = inputHandler.getDoubleInput("Enter item price:", new DoubleValidator(0.0, Double.MAX_VALUE));
        double weight = inputHandler.getDoubleInput("Enter item weight:", new DoubleValidator(0.0, Double.MAX_VALUE));

        Item newItem = itemFactory.createItem(itemType, name, description, price, weight, inputHandler);

        if (newItem != null) {
            inventoryService.addItem(newItem);
        }
    }
}

