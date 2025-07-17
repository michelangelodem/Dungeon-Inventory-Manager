package View;

import java.util.List;

import Items.Item;
import Items.Weapon;
import Items.Armor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ItemTableView {
    private TableView<Item> table;
    private ObservableList<Item> data;

    public ItemTableView(List<Item> items) {
        data = FXCollections.observableArrayList(items);
        table = new TableView<>(data);
        setupTable();
    }
    
    private void setupTable() {
        // Clear existing columns
        table.getColumns().clear();
        
        // Create basic columns
        setTableColumns("Name");

        TableColumn<Item, String> descriptionCol = new TableColumn<>("Description");
        
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(col -> new ItemDescriptionCell());
        descriptionCol.setPrefWidth(120); // Set a fixed width for the button column
        descriptionCol.setResizable(false);
        table.getColumns().add(descriptionCol);
        
        // Continue with other columns
        setTableColumns("Price");
        setTableColumns("Weight");

        TableColumn<Item, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            Item item = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(item.getClass().getSimpleName());
        });

        TableColumn<Item, String> extraCol = new TableColumn<>("Extra");
        extraCol.setCellValueFactory(cellData -> {
            Item item = cellData.getValue();
            if (item instanceof Weapon) {
                return new javafx.beans.property.SimpleStringProperty("Damage: " + ((Weapon) item).getDamageRoll());
            } else if (item instanceof Armor) {
                return new javafx.beans.property.SimpleStringProperty("Defense: " + ((Armor) item).getDefense());
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });

        table.getColumns().add(typeCol);
        table.getColumns().add(extraCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
        // Add some styling
        table.setStyle("-fx-background-color: #2d0f64ff; -fx-border-color: #2f6faeff; -fx-border-width: 1px; -fx-border-radius: 5px;");
    }

    private void setTableColumns(String name) {
        TableColumn<Item, String> col = new TableColumn<>(name);
        col.setCellValueFactory(new PropertyValueFactory<>(name));
        table.getColumns().add(col);
    }
    
    public void updateItems(List<Item> newItems) {
        data.clear();
        data.addAll(newItems);
        table.refresh();
    }

    public TableView<Item> getTableView() {
        return table;
    }
}