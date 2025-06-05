package Inventory;
import java.util.ArrayList;
import java.util.List;

import FileManagement.IFileService;
import Items.Item;

public class InventoryService implements IInventoryService {
    private List<Item> items;
    private InventoryCount itemCount;
    private IFileService fileService;
    
    public InventoryService(IFileService fileService) {
        this.fileService = fileService;
        this.items = new ArrayList<>();
        this.itemCount = new InventoryCount();
        loadInventory();
    }
    
    @Override
    public void addItem(Item item) {
        items.add(item);
        itemCount.changeItemCount(1);
    }
    
    @Override
    public boolean removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        
        Item removedItem = items.remove(index);
        itemCount.changeItemCount(-1);
        System.out.println("Removing item at index " + (index + 1) + ": ");
        removedItem.PrintItem();
        System.out.println("Item removed. " + getItemCount() + " items left in inventory.");
        return true;
    }
    
    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items); // Return defensive copy
    }
    
    @Override
    public List<Item> searchItemsByName(String name) {
        List<Item> foundItems = new ArrayList<>();
        for (Item item : items) {
            if (item.compareName(name)) {
                foundItems.add(item);
            }
        }
        return foundItems;
    }
    
    @Override
    public int getItemCount() {
        return itemCount.getItemCount();
    }
    
    @Override
    public void loadInventory() {
        List<Item> loadedItems = fileService.readItems();
        if (loadedItems != null && !loadedItems.isEmpty()) {
            items = loadedItems;
            itemCount.setItemCount(items.size());
            System.out.println("Loaded " + items.size() + " items from file: " + fileService.getFileName());
        } else {
            System.out.println("No items found in file. Starting with an empty inventory.");
        }
    }
    
    @Override
    public void saveInventory() {
        Item[] itemsArray = items.toArray(new Item[0]);
        if (fileService.writeItems(itemsArray)) {
            System.out.println("Items saved to file: " + fileService.getFileName());
        } else {
            System.out.println("Error saving items to file.");
        }
        itemCount.saveItemCountToFile("itemCount.txt");
    }
}