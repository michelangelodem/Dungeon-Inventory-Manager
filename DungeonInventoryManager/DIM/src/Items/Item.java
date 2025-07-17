package Items;

import java.io.BufferedWriter;
import java.io.IOException;
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

    protected abstract void writeSpecificData(BufferedWriter writer) throws IOException;

    public abstract void display();

        
    public void writeToStream(BufferedWriter writer) throws IOException {
        writer.write("Name: " + name + "\n");
        writer.write("Description: " + description + "\n");
        writer.write("Price: " + price + "\n");
        writer.write("Weight: " + weight + "\n");
        writeSpecificData(writer); // Call the abstract method
    }

    @Override
    public String toString() {
        return getName();
    }
}

