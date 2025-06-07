package Items;
import java.io.BufferedReader;
import java.io.IOException;
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

    @Override
    public String[] loadItemData(BufferedReader reader) {
            
        String[] itemData = new String[5];
        try {
            itemData = super.loadItemData(reader);
            if (itemData == null || itemData.length < 5) {
                System.out.println("Warning: Incomplete item data found in file for Weapon");
                return null;
            }
            String armorClass = reader.readLine();
            if (armorClass == null || armorClass.trim().isEmpty()) {
                System.out.println("Warning: Incomplete damage data found in file for Weapon");
                armorClass = "0";
            }
            itemData[4] = armorClass.trim();
            return itemData;
        } catch (IOException e) {
            System.out.println("Error reading item data from file: " + e.getMessage());
            return null;
        }
    }
} 
