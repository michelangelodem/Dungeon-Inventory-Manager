package Commands;

import FileManagement.FileService;
import Inventory.InventoryService;

public class ExitCommand implements ICommand {
    private InventoryService inventoryService;
    private FileService fileService;

    public ExitCommand(InventoryService inventoryService, FileService fileService) {
        this.inventoryService = inventoryService;
        this.fileService = fileService;
    }

    @Override
    public void execute() {
        fileService.writeItemsToFile(inventoryService.getAllItems(), "inventory.dat");
        System.out.println("Exiting Dungeon Inventory Manager. Goodbye!");
        System.exit(0);
    }
}


