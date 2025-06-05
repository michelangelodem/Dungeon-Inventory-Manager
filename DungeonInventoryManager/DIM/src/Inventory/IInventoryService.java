package Inventory;
import java.util.List;

import Items.Item;


public interface IInventoryService {
    void addItem(Item item);
    boolean removeItem(int index);
    List<Item> getAllItems();
    List<Item> searchItemsByName(String name);
    int getItemCount();
    void loadInventory();
    void saveInventory();
}
