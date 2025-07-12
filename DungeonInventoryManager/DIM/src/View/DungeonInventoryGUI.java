package View;

import java.util.List;

import FileManagement.FileService;
import Inventory.InventoryService;
import Items.Item;
import User.User;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class DungeonInventoryGUI extends Application {
    private static InventoryService inventoryService = new InventoryService();
    private static FileService fileService = new FileService();
    private Stage primaryStage;
    private User currentUser;
    private ItemTableView itemTableView;
    private InventoryActionsPanel actionsPanel;

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

        // Create the main layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Create header
        HBox header = createHeader();
        root.setTop(header);
        
        // Create main content area
        HBox mainContent = new HBox(15);
        mainContent.setPadding(new Insets(10, 0, 0, 0));
        
        // Create and set up the table view
        itemTableView = new ItemTableView(loadedItems);
        VBox tableContainer = new VBox(5);
        Label tableTitle = new Label("Your Inventory");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #495057;");
        tableContainer.getChildren().addAll(tableTitle, itemTableView.getTableView());
        
        // Create actions panel
        actionsPanel = new InventoryActionsPanel(inventoryService, currentUser, this::refreshInventoryView);
        
        // Add components to main content
        mainContent.getChildren().addAll(tableContainer, actionsPanel.getActionsPanel());
        HBox.setHgrow(tableContainer, javafx.scene.layout.Priority.ALWAYS);
        
        root.setCenter(mainContent);
        
        // Create scene
        Scene scene = new Scene(root, 1200, 600);
        
        primaryStage.setTitle("Dungeon Inventory Manager - " + user.getUsername());
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Save inventory when application closes
        primaryStage.setOnCloseRequest(e -> saveCurrentInventory());
    }
    
    private HBox createHeader() {
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 15, 0));
        header.setStyle("-fx-border-color: #dee2e6; -fx-border-width: 0 0 1 0;");
        
        Label titleLabel = new Label("Dungeon Inventory Manager");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #212529;");
        
        Label userLabel = new Label("Welcome, " + currentUser.getUsername() + "!");
        userLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6c757d; -fx-font-weight: bold;");
        
        // Create stats label
        Label statsLabel = new Label();
        updateStatsLabel(statsLabel);
        statsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 6px 12px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        logoutButton.setOnMouseEntered(e -> logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 6px 12px; -fx-background-color: #bd2130; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;"));
        logoutButton.setOnMouseExited(e -> logoutButton.setStyle("-fx-font-size: 12px; -fx-padding: 6px 12px; -fx-background-color: #dc3545; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;"));
        logoutButton.setOnAction(e -> handleLogout());
        
        // Layout components
        VBox leftSide = new VBox(5);
        leftSide.setAlignment(Pos.CENTER_LEFT);
        leftSide.getChildren().addAll(titleLabel, userLabel, statsLabel);
        
        HBox rightSide = new HBox();
        rightSide.setAlignment(Pos.CENTER_RIGHT);
        rightSide.getChildren().add(logoutButton);
        
        header.getChildren().addAll(leftSide, rightSide);
        HBox.setHgrow(rightSide, javafx.scene.layout.Priority.ALWAYS);
        
        return header;
    }
    
    private void updateStatsLabel(Label statsLabel) {
        List<Item> items = inventoryService.getAllItems();
        int totalItems = items.size();
        int weapons = (int) items.stream().filter(item -> item instanceof Items.Weapon).count();
        int armor = (int) items.stream().filter(item -> item instanceof Items.Armor).count();
        int regular = totalItems - weapons - armor;
        
        statsLabel.setText(String.format("Total Items: %d | Weapons: %d | Armor: %d | Regular: %d", 
                                        totalItems, weapons, armor, regular));
    }
    
    private void refreshInventoryView() {
        // Get current items from inventory service
        List<Item> currentItems = inventoryService.getAllItems();
        
        // Update the table view
        itemTableView.updateItems(currentItems);
        
        // Update stats in header - we need to find the stats label in the scene
        updateStatsInHeader();
    }
    
    private void updateStatsInHeader() {
        // This is a simple way to update stats - in a more complex app you'd want better state management
        Scene scene = primaryStage.getScene();
        if (scene != null && scene.getRoot() instanceof BorderPane) {
            BorderPane root = (BorderPane) scene.getRoot();
            if (root.getTop() instanceof HBox) {
                HBox header = (HBox) root.getTop();
                header.getChildren().stream()
                    .filter(node -> node instanceof VBox)
                    .findFirst()
                    .ifPresent(node -> {
                        VBox leftSide = (VBox) node;
                        leftSide.getChildren().stream()
                            .filter(child -> child instanceof Label)
                            .skip(2) // Skip title and user label
                            .findFirst()
                            .ifPresent(statsNode -> updateStatsLabel((Label) statsNode));
                    });
            }
        }
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