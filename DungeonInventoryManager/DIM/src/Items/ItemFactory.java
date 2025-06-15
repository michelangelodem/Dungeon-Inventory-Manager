package Items;

import InputValidation.DamageValidator;
import InputValidation.IInputHandler;
import InputValidation.NumberValidator;

public class ItemFactory implements IItemFactory {
    @Override
    public Item createItem(String itemType, String name, String description, double price, double weight, IInputHandler inputHandler) {
        if (itemType.equalsIgnoreCase("Weapon")) {
            String damageRoll = inputHandler.getStringInput("Enter weapon damage (e.g., 2d6, 1d8 + 2):", new DamageValidator());
            return new Weapon(name, description, price, weight, damageRoll);
        } else if (itemType.equalsIgnoreCase("Armor")) {
            int defense = inputHandler.getIntegerInput("Enter armor defense:", new NumberValidator(0, 1000));
            return new Armor(name, description, price, weight, defense);
        } else if (itemType.equalsIgnoreCase("Regular")) {
            return new RegularItem(name, description, price, weight);
        } else {
            System.out.println("Unknown item type. Item not created.");
            return null;
        }
    }
}

