package View;

import Items.Item;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;

public class ItemDescriptionCell extends TableCell<Item, String> {
    private final Button viewButton;
    
    public ItemDescriptionCell() {
        viewButton = new Button("View");
        viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 4px 8px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;");
        viewButton.setOnMouseEntered(e -> viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 4px 8px; -fx-background-color: #0056b3; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        viewButton.setOnMouseExited(e -> viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 4px 8px; -fx-background-color: #007bff; -fx-text-fill: white; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        
        viewButton.setOnAction(e -> {
            Item item = getTableView().getItems().get(getIndex());
            if (item != null) {
                showDescriptionDialog(item);
            }
        });
    }
    
    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty || item == null) {
            setGraphic(null);
        } else {
            setGraphic(viewButton);
        }
    }
    
    private void showDescriptionDialog(Item item) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Item Description - " + item.getName());
        dialogStage.setResizable(false);
        
        VBox dialogContent = new VBox(15);
        dialogContent.setPadding(new Insets(20));
        dialogContent.setStyle("-fx-background-color: #f8f9fa;");
        
        // Item name label
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("Item: " + item.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #212529;");
        
        // Description text area
        TextArea descriptionArea = new TextArea(item.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(8);
        descriptionArea.setPrefColumnCount(50);
        descriptionArea.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ced4da; -fx-border-width: 1px; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        
        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px 16px; -fx-background-color: #6c757d; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px 16px; -fx-background-color: #545b62; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-font-size: 12px; -fx-padding: 8px 16px; -fx-background-color: #6c757d; -fx-text-fill: white; -fx-border-radius: 4px; -fx-background-radius: 4px;"));
        closeButton.setOnAction(e -> dialogStage.close());
        
        // Center the close button
        javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox();
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        
        dialogContent.getChildren().addAll(nameLabel, descriptionArea, buttonBox);
        
        Scene dialogScene = new Scene(dialogContent, 400, 300);
        dialogStage.setScene(dialogScene);
        dialogStage.centerOnScreen();
        dialogStage.showAndWait();
    }
}