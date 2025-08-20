package Items;

import java.io.BufferedWriter;
import java.io.IOException;

public class Armor extends Item {
    private int defense;

    public Armor(String name, String description, double price, double weight, int defense) {
        super(name, description, price, weight);
        this.defense = defense;
    }

    public int getDefense() {
        return defense;
    }

    @Override
    public void display() {
        System.out.println("Armor: " + getName() + ", Description: " + getDescription() + ", Price: " + getPrice() + ", Weight: " + getWeight() + ", Defense: " + defense);
    }

    @Override 
    protected void writeSpecificData(BufferedWriter writer) throws IOException {
        writer.write("Armor Class:" + defense + "\n");
    }
    
    @Override
    public String toString() {
        return getName() + " (Armor Class: " + defense + ")";
    }
}

