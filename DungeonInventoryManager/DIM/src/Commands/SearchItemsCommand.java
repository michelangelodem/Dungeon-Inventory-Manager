package Commands;
import java.util.List;
import java.util.Scanner;

import Inventory.IInventoryService;
import Items.Item;


public class SearchItemsCommand implements ICommand {
    private final IInventoryService inventoryService;
    private final Scanner scanner;
    
    public SearchItemsCommand(IInventoryService inventoryService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.scanner = scanner;
    }
    
    @Override
    public void execute() {
        System.out.println("Searching for item...\n");
        System.out.print("Enter item name to search: ");
        String name = scanner.nextLine();
        
        List<Item> foundItems = inventoryService.searchItemsByName(name);
        
        if (foundItems.isEmpty()) {
            System.out.println("No items found with the name: " + name);
        } else {
            System.out.println("Found items: ");
            for (Item item : foundItems) {
                item.PrintItem();
                System.out.println(); // Add spacing between items
            }
        }
    }
    
    @Override
    public String getDescription() {
        return "Search Item by Name";
    }
    
    @Override
    public int getCommandId() {
        return 4;
    }
}