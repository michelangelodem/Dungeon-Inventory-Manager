package Items;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;
import InputValidation.DamageValidator;
import InputValidation.InputHandler; 

public class Weapon extends Item {
    private String damage;

    public Weapon() {
        super();
        this.damage = null;

    }

    public Weapon(String name, String description, double price, double weight, String damage) {
        super(name, description, price, weight);
        this.damage = damage;
    }

    public String getDamage() {
        return damage;
    }

    private void setDamage(String damage) {
        InputHandler inputHandler = new InputHandler();
        DamageValidator damageValidator = new DamageValidator(damage);
        this.damage = inputHandler.getValidatedInput("Enter valid damage (e.g., 1d6 + 2): ", damageValidator);
    }

    @Override

    public void PrintItem() {
        super.PrintItem();
        System.out.println("Damage: " + getDamage());
    }

    @Override
    public void readItem(Scanner scanner) {
        super.readItem(scanner);
        
        while (true) {
            try {
                System.out.print("Enter item damage: ");
                String inputDamage = scanner.nextLine();
                setDamage(inputDamage);
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
            setDamage(itemData[4]);
        } catch (IllegalArgumentException e) {
            System.out.println("Warning, Invalid damage value in file: " + e.getMessage());
            this.setDamage("1d6"); // Default damage if invalid
        }
    }

    @Override
    public String toString() {
        return super.toString().replace("}", ", damage='" + damage + "'}");
    }

    public int getMaxDamage() {    
        String[] parts = damage.split(" \\+ ");
        int maxDamage = 0;
            
        for (String part : parts) {
            maxDamage += calculateParseMaxDamage(part);
        }            
        return maxDamage;
    }

    private int calculateParseMaxDamage(String part) {
        String[] diceParts = part.split("d");
        if (diceParts.length != 2) {
            System.out.println("Invalid damage format: " + part);
            return 0;
        }
        
        try {
            int numDice = Integer.parseInt(diceParts[0].trim());
            int diceType = Integer.parseInt(diceParts[1].trim());
            int maxParseDamage = numDice * diceType; // Max damage is numDice * max face value of the die
            return maxParseDamage;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in damage: " + part);
        }
        return 0;
    }

    public int getMinDamage() {       
        String[] parts = damage.split(" \\+ ");
        int minDamage = 0;
            
        for (String part : parts) {
            minDamage += calculateParseMinDamage(part);
        }
        return minDamage;
    }

    private int calculateParseMinDamage(String part) {
        String[] diceParts = part.split("d");
        if (diceParts.length != 2) {
            System.out.println("Invalid damage format: " + part);
            return 0;
        }
        
        try {
            int numDice = Integer.parseInt(diceParts[0].trim());
            int minParseDamage = numDice; // Min damage is numDice * 1 (minimum face value of the die)
            return minParseDamage;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format in damage: " + part);
        }
        return 0;
    }
    
    @Override
    public String[] loadItemData(BufferedReader reader) {

        try {
            String[] itemData = super.loadItemData(reader);
            if (itemData == null || itemData.length < 4) {
                System.out.println("Warning: Incomplete item data found in file for Weapon");
                return null;
            }

            String damage = reader.readLine();
            if (damage == null || damage.trim().isEmpty()) {
                System.out.println("Warning: Incomplete damage data found in file for Weapon");
                damage = "1d6"; // Default damage if not provided
            }
            String[] weaponData = {
                itemData[0], // name
                itemData[1], // description
                itemData[2], // price
                itemData[3], // weight
                damage.trim() // damage
            };

            return weaponData ;
        } catch (IOException e) {
            System.out.println("Error reading item data from file: " + e.getMessage());
            return null;
        }
    }

}
