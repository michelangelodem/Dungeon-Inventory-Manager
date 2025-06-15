package Commands;

import java.util.List;
import DnDMechanics.DamageCalculator;
import DnDMechanics.RollD20;
import Inventory.IInventoryService;
import Items.Weapon;
import InputValidation.IInputHandler;
import InputValidation.NumberValidator;

public class MakeAttackCommand implements ICommand {
    private IInventoryService inventoryService;
    private IInputHandler inputHandler;
    private RollD20 rollD20;
    private DamageCalculator damageCalculator;

    // Threshold to pass for a successful attack, can be adjusted based on game rules
    private int threshold_to_pass = 5; 

    private void setThresholdToPass(int threshold) {
        this.threshold_to_pass = threshold;
    }

    private void setThresholdToPassFromUser() {
        System.out.println("Enter the threshold to pass for a successful attack (default is 5):");
        int userThreshold = inputHandler.getIntegerInput("Threshold:", new NumberValidator(1, 30));
        setThresholdToPass(userThreshold);
    }

    public MakeAttackCommand(IInventoryService inventoryService, IInputHandler inputHandler) {
        this.inventoryService = inventoryService;
        this.inputHandler = inputHandler;
        this.rollD20 = new RollD20();
        this.damageCalculator = new DamageCalculator();
    }

    @Override
    public void execute() {
        List<Weapon> weapons = inventoryService.getAllWeapons();
        if (weapons.isEmpty()) {
            System.out.println("No weapons in inventory to make an attack.");
            return;
        }

        System.out.println("Available Weapons:");
        for (int i = 0; i < weapons.size(); i++) {
            System.out.println((i + 1) + ". " + weapons.get(i).getName() + " (Damage: " + weapons.get(i).getDamageRoll() + ")");
        }

        int weaponChoice = inputHandler.getIntegerInput("Choose a weapon by number:", new NumberValidator(1, weapons.size()));
        Weapon chosenWeapon = weapons.get(weaponChoice - 1);

        setThresholdToPassFromUser();

        int attackRoll = rollD20.roll(inputHandler);
        System.out.println("You rolled a " + attackRoll + " on your d20.");

        if (attackRoll >= threshold_to_pass) { // Simple hit condition
            int totalDamage = damageCalculator.calculateDamage(chosenWeapon.getDamageRoll());
            System.out.println("You hit! Dealing " + totalDamage + " damage with your " + chosenWeapon.getName() + ".");
            setThresholdToPass(5);
        } else {
            System.out.println("You missed!");
            setThresholdToPass(5);
        }
    }
}

