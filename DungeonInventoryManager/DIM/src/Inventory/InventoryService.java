package Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import Items.Item;
import Items.Weapon;

public class InventoryService implements IInventoryService {
    private List<Item> items = new ArrayList<>();

    @Override
    public void addItem(Item item) {
        this.items.add(item);
        System.out.println(item.getName() + " added to inventory.");
    }

    @Override
    public void removeItem(String name) {
        if (items.removeIf(item -> item.getName().equalsIgnoreCase(name))) {
            System.out.println(name + " removed from inventory.");
        } else {
            System.out.println(name + " not found in inventory.");
        }
    }

    @Override
    public void viewAllItems() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        items.forEach(Item::display);
    }

    @Override
    public List<Item> searchItem(String name) {
        return items.stream()
                .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Weapon> getAllWeapons() {
        return items.stream()
                .filter(item -> item instanceof Weapon)
                .map(item -> (Weapon) item)
                .collect(Collectors.toList());
    }
}

