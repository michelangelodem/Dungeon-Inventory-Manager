package main;

import Commands.*;

import Inventory.InventoryService;
import InputValidation.IInputHandler;
import InputValidation.InputHandler;
import Items.Item;
import Items.ItemFactory;
import Items.IItemFactory;
import FileManagement.FileService;
import java.util.Scanner;
import java.util.List;


public class InventoryManagementSystem {

    private static InventoryService inventoryService = new InventoryService();
    private static CommandManager commandManager = new CommandManager();
    private static Scanner scanner = new Scanner(System.in);
    private static IInputHandler inputHandler = new InputHandler(scanner);
    private static IItemFactory itemFactory = new ItemFactory();
    private static FileService fileService = new FileService();

    public static void main(String[] args) {
        // Load inventory at startup
        List<Item> loadedItems = fileService.readItemsFromFile("inventory.dat");
        loadedItems.forEach(item -> inventoryService.addItem(item)); // Add loaded items to the service

        initializeCommands();
        runSystem();
    }

    private static void initializeCommands() {
        commandManager.addCommand("1", new AddItemCommand(inventoryService, inputHandler, itemFactory));
        commandManager.addCommand("2", new RemoveItemCommand(inventoryService, inputHandler));
        commandManager.addCommand("3", new ViewItemsCommand(inventoryService));
        commandManager.addCommand("4", new SearchItemsCommand(inventoryService, inputHandler));
        commandManager.addCommand("5", new MakeAttackCommand(inventoryService, inputHandler));
        commandManager.addCommand("6", new ExitCommand(inventoryService, fileService));
    }

    private static void runSystem() {
        System.out.println("Welcome to Dungeon Inventory Manager!");
        while (true) {
            System.out.println("\nEnter command number:");
            System.out.println("1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. View Items");
            System.out.println("4. Search Items");
            System.out.println("5. Make Attack");
            System.out.println("6. Exit");
            String command = scanner.nextLine().toLowerCase();
            commandManager.executeCommand(command);
        }
    }
}



