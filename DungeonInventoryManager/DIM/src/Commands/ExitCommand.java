package Commands;
import Inventory.IInventoryService;

public class ExitCommand implements ICommand {
    private final IInventoryService inventoryService;
    private boolean exitRequested = false;
    
    public ExitCommand(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @Override
    public void execute() {
        System.out.println("Saving inventory and exiting...");
        inventoryService.saveInventory();
        exitRequested = true;
        System.out.println("Thank you for using the Inventory Management System.");
    }
    
    @Override
    public String getDescription() {
        return "Exit";
    }
    
    @Override
    public int getCommandId() {
        return 0;
    }
    
    public boolean isExitRequested() {
        return exitRequested;
    }
}