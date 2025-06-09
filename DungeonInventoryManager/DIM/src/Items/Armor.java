package Items;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import InputValidation.NumberValidator;

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

    public void setArmorClass(String armorClass) {
        NumberValidator armorClassValidator = new NumberValidator(armorClass);
        armorClass = inputHandler.getValidatedInput(armorClassValidator);
        this.armorClass = Integer.parseInt(armorClass);
    }

     @Override

    public void PrintItem() {
        super.PrintItem();
        System.out.println("Armor class: " + getArmorClass());
    }

    @Override
    public void readItem(Scanner scanner) {
        super.readItem(scanner);
        
        while (true) {
            try {
                System.out.print("Enter armor's class: ");
                String inputAC = scanner.nextLine();
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
            this.armorClass = Integer.parseInt(itemData[4]);
        } catch (IllegalArgumentException e) {
            System.out.println("Warning, Invalid damage value in file: " + e.getMessage());// Default AC if invalid
        }
    }

    @Override
    public String toString() {
        return super.toString().replace("}", ", ArmorClass=" + armorClass + "'}");
    }

    @Override
    public String[] loadItemData(BufferedReader reader) {
            
        try {
            String[] itemData = super.loadItemData(reader);
            if (itemData == null || itemData.length < 4) {
                System.out.println("Warning: Incomplete item data found in file for Weapon");
                return null;
            }
            String armorClass = reader.readLine();
            if (armorClass == null || armorClass.trim().isEmpty()) {
                System.out.println("Warning: Incomplete damage data found in file for Weapon");
                armorClass = "0";
            }
            String[] armorData = {
                itemData[0], // Name
                itemData[1], // Description
                itemData[2], // Price
                itemData[3], // Weight
                armorClass.trim() // Armor Class
            };
            return armorData;
        } catch (IOException e) {
            System.out.println("Error reading item data from file: " + e.getMessage());
            return null;
        }
    }
} 
