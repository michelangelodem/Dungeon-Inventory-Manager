package Inventory;
import Items.*;

import java.util.List;

public interface IInventoryService {
    void addItem(Item item);
    void removeItem(String name);
    void viewAllItems();
    List<Item> searchItem(String name);
    List<Weapon> getAllWeapons();
}

