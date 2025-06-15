package Items;

import java.io.Serializable;

public abstract class Item implements Serializable {
    private String name;
    private String description;
    private double price;
    private double weight;

    public Item(String name, String description, double price, double weight) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getWeight() {
        return weight;
    }

    public abstract void display();
}

