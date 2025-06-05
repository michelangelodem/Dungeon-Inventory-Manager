package Commands;
import java.util.Scanner;

import Inventory.IInventoryService;


public class RemoveItemCommand implements ICommand {
    private final IInventoryService inventoryService;
    private final Scanner scanner;
    
    public RemoveItemCommand(IInventoryService inventoryService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.scanner = scanner;
    }
    
    @Override
    public void execute() {
        System.out.println("Removing item...\n");
        
        if (inventoryService.getItemCount() == 0) {
            System.out.println("No items to remove.\n");
            return;
        }
        
        System.out.println("Enter the index of the item to remove (1 to " + 
                          inventoryService.getItemCount() + "): ");
        
        try {
            int index = scanner.nextInt() - 1;
            scanner.nextLine(); // Consume leftover newline
            
            if (!inventoryService.removeItem(index)) {
                System.out.println("Invalid index. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine(); // Clear invalid input
        }
    }
    
    @Override
    public String getDescription() {
        return "Remove Item";
    }
    
    @Override
    public int getCommandId() {
        return 2;
    }
} 