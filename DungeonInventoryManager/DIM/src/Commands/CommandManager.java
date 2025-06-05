package Commands;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import Inventory.IInventoryService;

public class CommandManager {
    private Map<Integer, ICommand> commands;
    private Scanner scanner;
    private ExitCommand exitCommand;
    
    public CommandManager(IInventoryService inventoryService, Scanner scanner) {
        this.scanner = scanner;
        this.commands = new HashMap<>();
        initializeCommands(inventoryService);
    }
    
    private void initializeCommands(IInventoryService inventoryService) {
        // Initialize all commands
        exitCommand = new ExitCommand(inventoryService);
        
        commands.put(0, exitCommand);
        commands.put(1, new AddItemCommand(inventoryService, scanner));
        commands.put(2, new RemoveItemCommand(inventoryService, scanner));
        commands.put(3, new ViewItemsCommand(inventoryService));
        commands.put(4, new SearchItemsCommand(inventoryService, scanner));
        commands.put(5, new MakeAttackCommand(inventoryService, scanner));
        commands.put(6, new GenerateImageCommand(scanner));
    }
    
    public void displayMenu() {
        System.out.println("\n=== Inventory Management System ===");
        for (Map.Entry<Integer, ICommand> entry : commands.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue().getDescription());
        }
        System.out.print("Please enter your choice: ");
    }
    
    public boolean executeCommand() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume leftover newline
            
            ICommand command = commands.get(choice);
            if (command != null) {
                command.execute();
                return !exitCommand.isExitRequested();
            } else {
                System.out.println("Invalid command. Please try again.");
                return true;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear invalid input
            return true;
        }
    }
    
    public void run() {
        System.out.println("Welcome to the Inventory Management System!");
        
        while (true) {
            displayMenu();
            if (!executeCommand()) {
                break;
            }
        }
    }
}