package Items;

import java.io.BufferedWriter;
import java.io.IOException;

public class Weapon extends Item {
    private String damageRoll;

    public Weapon(String name, String description, double price, double weight, String damageRoll) {
        super(name, description, price, weight);
        this.damageRoll = damageRoll;
    }

    public String getDamageRoll() {
        return damageRoll;
    }

    @Override
    public void display() {
        System.out.println("Weapon: " + getName() + ", Description: " + getDescription() + ", Price: " + getPrice() + ", Weight: " + getWeight() + ", Damage: " + damageRoll);
    }

    @Override
    protected void writeSpecificData(BufferedWriter writer) throws IOException {
        writer.write("Damage: " + damageRoll + "\n");
    }

    @Override
    public String toString() {
        return getName() + " (Damage: " + damageRoll + ")";
    }
}

