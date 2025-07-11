package View;

import java.util.List;

import FileManagement.FileService;
import Inventory.InventoryService;
import Items.Item;

import javafx.scene.layout.VBox;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class DungeonInventoryGUI extends Application {
    private static InventoryService inventoryService = new InventoryService();
    private static FileService fileService = new FileService();
    private static final String INVENTORY_FILE = "inventory.dat";

    @Override
    public void start(Stage primaryStage) {
        // Load inventory from file at startup
        List<Item> loadedItems = fileService.readItemsFromFile(INVENTORY_FILE);
        loadedItems.forEach(item -> inventoryService.addItem(item));

        ItemTableView itemTableView = new ItemTableView(loadedItems);

        VBox root = new VBox();
        root.getChildren().add(itemTableView.getTableView());

        // Add a label to the root layout
        Label titleLabel = new Label("Dungeon Inventory Manager");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-padding: 10px;");
        root.getChildren().add(titleLabel);

        // Create a scene with the layout pane
        Scene scene = new Scene(root, 1000, 500); // width, height

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

