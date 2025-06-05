package Inventory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InventoryCount {
    private static final String DEFAULT_FILE_NAME = "itemCount.txt";
    private int itemCount = 0;

    public InventoryCount() {
        this.itemCount = 0;
    }

    public InventoryCount(int initialCount) {
        if (initialCount < 0) {
            throw new IllegalArgumentException("Item count cannot be negative");
        }
        this.itemCount = initialCount;
    }

    public void changeItemCount(int change) {
        int newCount = this.itemCount + change;
        if (newCount < 0) {
            throw new IllegalArgumentException("Item count cannot become negative");
        }
        this.itemCount = newCount;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int desiredItemCount) {
        if (desiredItemCount < 0) {
            throw new IllegalArgumentException("Item count cannot be negative");
        }
        this.itemCount = desiredItemCount;
    }

    public void incrementCount() {
        this.itemCount++;
    }

    public void decrementCount() {
        if (this.itemCount > 0) {
            this.itemCount--;
        }
    }

    public void resetCount() {
        this.itemCount = 0;
    }

    public boolean saveItemCountToFile(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = DEFAULT_FILE_NAME;
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(String.valueOf(itemCount));
            return true;
        } catch (IOException e) {
            System.out.println("Error writing item count to file: " + e.getMessage());
            return false;
        }
    }

    public boolean saveItemCountToFile() {
        return saveItemCountToFile(DEFAULT_FILE_NAME);
    }

    @Override
    public String toString() {
        return "InventoryCount{itemCount=" + itemCount + "}";
    }
}