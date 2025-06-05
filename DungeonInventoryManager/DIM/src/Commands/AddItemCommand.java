package Commands;
import java.util.Scanner;
import Items.*;
import Inventory.IInventoryService;

public class AddItemCommand implements ICommand {
    private final IInventoryService inventoryService;
    private final Scanner scanner;
    
    public AddItemCommand(IInventoryService inventoryService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.scanner = scanner;
    }
    
    @Override
    public void execute() {
        System.out.println("Adding item...\n");
        
        // Ask user what type of item to add
        System.out.println("What type of item would you like to add?");
        System.out.println("1. Regular Item");
        System.out.println("2. Sword");
        System.out.println("3. Armor");
        System.out.print("Enter your choice: ");
        
        try {
            int itemType = scanner.nextInt();
            scanner.nextLine(); // Consume leftover newline
            
            Item newItem;
            switch (itemType) {
                case 1:
                    newItem = new Item();
                    break;
                case 2:
                    newItem = new Weapon();
                    break;
                case 3:
                    newItem = new Armor();
                    break;
                default:
                    System.out.println("Invalid choice. Adding regular item.");
                    newItem = new Item();
                    break;
            }
            
            newItem.readItem(scanner);
            inventoryService.addItem(newItem);
            System.out.println(inventoryService.getItemCount() + " Items in Inventory.\n");
            
        } catch (Exception e) {
            System.out.println("Invalid input. Adding regular item.");
            scanner.nextLine(); // Clear invalid input
            Item newItem = new Item();
            newItem.readItem(scanner);
            inventoryService.addItem(newItem);
            System.out.println(inventoryService.getItemCount() + " Items in Inventory.\n");
        }
    }
    
    @Override
    public String getDescription() {
        return "Add Item";
    }
    
    @Override
    public int getCommandId() {
        return 1;
    }
}