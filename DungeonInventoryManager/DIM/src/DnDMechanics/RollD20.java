package DnDMechanics;
import java.util.Scanner;

import Commands.ICommand;

public class RollD20 implements ICommand {
    private final Scanner scanner;

    public RollD20(Scanner scanner) {
        this.scanner = scanner;
        System.out.println("Welcome to the D20 Roller!");
        System.out.println("You can roll a D20 and add modifiers to your roll.");
        System.out.println("Type 'done' when you are finished adding modifiers.\n");
    }

    @Override
    public void execute() {
        System.out.println("Rolling a D20...\n");

        int modifierSum = collectModifierSum();
        int roll = rollD20() + modifierSum; 
        
        System.out.println("You rolled a " + (roll - modifierSum) + " on the D20!");
        if (modifierSum != 0) {
            System.out.println("With modifiers, your total is: " + roll);
        } else {
            System.out.println("No modifiers added.");
        }
    }

    private int rollD20() {
        return (int) (Math.random() * 20) + 1; // Generates a random number between 1 and 20
    }

    public int collectModifierSum() {
        System.out.print("Enter any modifiers to add (separated by spaces, or 'done' to finish): ");
        String input = scanner.nextLine();

        if (input.equalsIgnoreCase("done")) {
            return 0; // No modifiers to add
        }

        String[] parts = input.split(" ");
        int sum = 0;
        for (String part : parts) {
            try {
                sum += Integer.parseInt(part); // Try to parse each part as an integer
            } catch (NumberFormatException e) {
                System.out.println("Invalid modifier: " + part + ". Ignoring this value.");
            }
        }
        return sum;
    }

    @Override
    public String getDescription() {
        return "Roll D20";
    }

    @Override
    public int getCommandId() {
        return 6; // Unique ID for this command
    }
    
}
