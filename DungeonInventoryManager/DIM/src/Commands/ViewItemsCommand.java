package Commands;
import java.util.List;

import Inventory.IInventoryService;
import Items.Item;


public class ViewItemsCommand implements ICommand {
    private final IInventoryService inventoryService;
    
    public ViewItemsCommand(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @Override
    public void execute() {
        System.out.println("Viewing items...\n");
        
        List<Item> items = inventoryService.getAllItems();
        
        if (items.isEmpty()) {
            System.out.println("No items in inventory.\n");
            return;
        }
        
        System.out.println("Items in inventory: ");
        for (int i = 0; i < items.size(); i++) {
            System.out.println("Item " + (i + 1) + ": ");
            items.get(i).PrintItem();
            System.out.println(); // Add spacing between items
        }
    }
    
    @Override
    public String getDescription() {
        return "View Items";
    }
    
    @Override
    public int getCommandId() {
        return 3;
    }
}
