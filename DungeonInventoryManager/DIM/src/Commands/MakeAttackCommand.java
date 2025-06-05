package Commands;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import DnDMechanics.RollD20;
import Inventory.IInventoryService;
import Items.Item;
import Items.Weapon;

public class MakeAttackCommand implements ICommand {
    private final IInventoryService inventoryService;
    private final Scanner scanner;
    private final Random random;

    public MakeAttackCommand(IInventoryService inventoryService, Scanner scanner) {
        this.inventoryService = inventoryService;
        this.scanner = scanner;
        this.random = new Random();
    }

    @Override
    public void execute() {
        System.out.println("Making an attack...\n");

        List<Weapon> weapons = loadWeapons();
        if (weapons == null) {
            return; // No weapons available, exit the method
        }

        Weapon selectedWeapon = slectWeaponToAttack(weapons);
        makeAttackRoll();
        int damage = makeDamageRoll(selectedWeapon);
        System.out.println("You attacked with " + selectedWeapon.getName() + " and dealt " + damage + " damage!");
    }

    private List<Weapon> loadWeapons() {
        List<Item> items = inventoryService.getAllItems();
        List<Weapon> weapons = items.stream()
            .filter(item -> item instanceof Weapon)
            .map(item -> (Weapon) item)
            .collect(java.util.stream.Collectors.toList());
        if (weapons.isEmpty()) {
            System.out.println("No weapons available in inventory.");
            return null;
        } else {
            return weapons;
        }
    }
    
    
    private Weapon slectWeaponToAttack(List<Weapon> weapons) {
        
        Weapon emptySword = new Weapon();
        
        System.out.println("Available weapons:");
        for (int i = 0; i < weapons.size(); i++) {
            System.out.println((i + 1) + ". " + weapons.get(i).getName());
        }
        
        System.out.print("Select a weapon by number: ");
        
        int weaponIndex;
        try {
            weaponIndex = scanner.nextInt() - 1; // Convert to zero-based index
            scanner.nextLine(); // Consume leftover newline
            if (weaponIndex < 0 || weaponIndex >= weapons.size()) {
                System.out.println("Invalid selection. No attack made.");
                return emptySword;
            }
        } catch (Exception e) {
            System.out.println("Invalid input. No attack made.");
            scanner.nextLine(); // Clear invalid input
            return emptySword;
        }
        Weapon selectedWeapon = weapons.get(weaponIndex);
        return selectedWeapon;
    }
    
    private int makeDamageRoll(Weapon selectedWeapon) {
        return random.nextInt(selectedWeapon.getMaxDamage() - selectedWeapon.getMinDamage() + 1) + selectedWeapon.getMinDamage();
    }
    
    // An attack roll in Dungeons & Dragons typically involves rolling a 20-sided die (d20) 
    //and adding modifiers based on the character's abilities, skills, or equipment.
    //This in order to determine if the attack hits the target.
    private void makeAttackRoll() {
        RollD20 dice = new RollD20(scanner);
        System.out.println("Attack roll: ");
        dice.execute();
    }

    @Override
        public String getDescription() {
        return "Make Attack";
    }
    
    @Override
    public int getCommandId() {
        return 5;
    }

}
