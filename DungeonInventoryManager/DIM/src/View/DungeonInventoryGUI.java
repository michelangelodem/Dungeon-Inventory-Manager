package View;

import java.util.List;

import FileManagement.FileService;
import Inventory.InventoryService;
import Items.Item;
import User.User;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class DungeonInventoryGUI extends Application {
    private static InventoryService inventoryService = new InventoryService();
    private static FileService fileService = new FileService();
    private Stage primaryStage;
    private User currentUser;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Show authentication view first
        AuthView authView = new AuthView(primaryStage, this::showMainInventory);
        authView.show();
    }
    
    private void showMainInventory(User user) {
        this.currentUser = user;
        
        // Load inventory from user-specific file
        String userInventoryFile = user.getInventoryFileName();
        List<Item> loadedItems = fileService.readItemsFromFile(userInventoryFile);
        
        // Clear previous inventory and add loaded items
        inventoryService = new InventoryService(); // Reset service
        loadedItems.forEach(item -> inventoryService.addItem(item));

        ItemTableView itemTableView = new ItemTableView(loadedItems);

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        // Header section with user info and logout
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        
        Label titleLabel = new Label("Dungeon Inventory Manager");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label userLabel = new Label("Welcome, " + user.getUsername() + "!");
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;");
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #c0392b; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        logoutButton.setOnAction(e -> handleLogout());
        
        // Add spacing between elements
        HBox leftSide = new HBox(20);
        leftSide.setAlignment(Pos.CENTER_LEFT);
        leftSide.getChildren().addAll(titleLabel, userLabel);
        
        HBox rightSide = new HBox();
        rightSide.setAlignment(Pos.CENTER_RIGHT);
        rightSide.getChildren().add(logoutButton);
        
        header.getChildren().addAll(leftSide, rightSide);
        HBox.setHgrow(rightSide, javafx.scene.layout.Priority.ALWAYS);
        
        // Add header and table to root
        root.getChildren().addAll(header, itemTableView.getTableView());
        VBox.setVgrow(itemTableView.getTableView(), javafx.scene.layout.Priority.ALWAYS);

        // Create a scene with the layout pane
        Scene scene = new Scene(root, 1000, 500);

        primaryStage.setTitle("Dungeon Inventory Manager - " + user.getUsername());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Save inventory when application closes
        primaryStage.setOnCloseRequest(e -> saveCurrentInventory());
    }
    
    private void handleLogout() {
        // Save current inventory before logout
        saveCurrentInventory();
        
        // Show authentication view again
        AuthView authView = new AuthView(primaryStage, this::showMainInventory);
        authView.show();
    }
    
    private void saveCurrentInventory() {
        if (currentUser != null) {
            // Save inventory to user-specific file
            String userInventoryFile = currentUser.getInventoryFileName();
            fileService.writeItemsToFile(inventoryService.getAllItems(), userInventoryFile);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}