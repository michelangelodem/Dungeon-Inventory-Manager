package Items;
import java.util.Scanner;

public class Armor extends Item {
    private int armorClass;
    
    public Armor() {
        super();
        this.armorClass = 0; // Default armor class
    }

    public Armor(int armorClass, String name, String description, double price, double weight) {
        super(name, description, price, weight);
        this.armorClass = armorClass;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public void setArmorClass(int armorClass) {
        if (armorClass < 0) {
            armorClass = 0;
        }
        this.armorClass = armorClass;
    }

     @Override

    public void PrintItem() {
        super.PrintItem();
        System.out.println("Damage: " + getArmorClass());
    }

    @Override
    public void readItem(Scanner scanner) {
        super.readItem(scanner);
        
        while (true) {
            try {
                System.out.print("Enter armor's class: ");
                int inputAC = scanner.nextInt();
                setArmorClass(inputAC);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }
    }
    
    @Override

    public void fromStr2Item(String[] itemData) {
        if (itemData.length < 5) {
            throw new IllegalArgumentException("Invalid item data: insufficient fields for Sword");
        }

        super.fromStr2Item(itemData);
        try {
            setArmorClass(Integer.parseInt(itemData[4]));
        } catch (IllegalArgumentException e) {
            System.out.println("Warning, Invalid damage value in file: " + e.getMessage());
            this.setArmorClass(0); // Default AC if invalid
        }
    }

    @Override
    public String toString() {
        return super.toString().replace("}", ", ArmorClass=" + armorClass + "'}");
    }

} 
