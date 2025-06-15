package Inventory;
import Items.Item;

public class InventoryCount {
    private Item item;
    private int count;

    public InventoryCount(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        this.count++;
    }

    public void decrementCount() {
        this.count--;
    }
}