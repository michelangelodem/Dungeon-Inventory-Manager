package main;
import java.util.Scanner;
import Inventory.InventoryService;
import Inventory.IInventoryService;
import Commands.CommandManager;
import FileManagement.FileService;
import FileManagement.IFileService;



public class InventoryManagementSystem {
    
    public static void main(String[] args) {
        // Dependency injection setup
        Scanner scanner = new Scanner(System.in);
        IFileService fileService = new FileService("items.txt");
        IInventoryService inventoryService = new InventoryService(fileService);
        
        // Create and run command manager
        CommandManager commandManager = new CommandManager(inventoryService, scanner);
        
        try {
            commandManager.run();
        } finally {
            scanner.close();
        }
    }
}
