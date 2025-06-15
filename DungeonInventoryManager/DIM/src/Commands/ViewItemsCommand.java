package Commands;

import Inventory.IInventoryService;

public class ViewItemsCommand implements ICommand {
    private IInventoryService inventoryService;

    public ViewItemsCommand(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void execute() {
        inventoryService.viewAllItems();
    }
}

