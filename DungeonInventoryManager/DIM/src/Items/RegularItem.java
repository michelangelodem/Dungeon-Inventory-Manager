package Items;

public class RegularItem extends Item {
    public RegularItem(String name, String description, double price, double weight) {
        super(name, description, price, weight);
    }

    @Override
    public void display() {
        System.out.println("Item: " + getName() + ", Description: " + getDescription() + ", Price: " + getPrice() + ", Weight: " + getWeight());
    }
}

