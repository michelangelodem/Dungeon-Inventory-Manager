package Items;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

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

    public void setDamage(String damage) {
        if (checkIfValidDamageString(damage)) {    
            this.damage = damage;
        } else {
            System.out.println("Invalid damage value. Please enter a valid string in form dice_num d dice_type.");
        }
        
    }

    /*
    * In DnD, damage is typically represented as a string in the form "XdY", 
    * where X is the number of dice and Y is the type of dice (e.g., "1d6" means one six-sided die).
    * But many times a epon can deal more than one type of damage,
    * so we can have a string like "1d6 + 2d8" or "1d6 + 1d10".
    * checkIfValidDamage checks if the damage string is valid.
    * It should be in the form "XdY" or "XdY + XdY", where X and Y are integers.
    * checkIfValidDamageString checks the total damage string for validity.
    * It should be in the form "XdY" or "XdY + XdY", where X and Y are integers.
    */
    
    private boolean checkIfValidDamageString(String damage) {
        if (damage.indexOf(" + ") != -1) {
            String[] parts = damage.split(" \\+ ");
            for (String part : parts) {
                if (!checkIfValidDamage(part)) {
                    return false;
                }
            }
            return true;
        } else {
            return checkIfValidDamage(damage);
        }
    }

    private boolean checkIfValidDamage(String damage) {
            if (damage == null || damage.trim().isEmpty()) {
                return false;
            }
            
            String[] parts = damage.split("d");
            if (parts.length != 2) {
                return false;
            }
            
            try {
                int num = Integer.parseInt(parts[0].trim());
                int type = Integer.parseInt(parts[1].trim());
                return num > 0 && type > 0;
            } catch (NumberFormatException e) {
                return false;
            }
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
        
        if(checkIfValidDamageString(damage)) {
        
            String[] parts = damage.split(" \\+ ");
            int maxDamage = 0;
            
            for (String part : parts) {
                maxDamage += calculateParseMaxDamage(part);
            }
            
            return maxDamage;
        }
        return 0;
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
        
        if(checkIfValidDamageString(damage)) {
        
            String[] parts = damage.split(" \\+ ");
            int minDamage = 0;
            
            for (String part : parts) {
                minDamage += calculateParseMinDamage(part);
            }
            
            return minDamage;
        }
        return 0;
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
