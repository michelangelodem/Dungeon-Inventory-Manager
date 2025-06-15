package Items;

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
}

