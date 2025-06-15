package DnDMechanics;

import java.util.Random;
import InputValidation.IInputHandler;
import InputValidation.NumberValidator;

public class RollD20 {

    public final int ROLL_WITH_ADVANTAGE = 1;
    public final int ROLL_WITH_DISADVANTAGE = 2;
    public final int ROLL_NORMAL = 0;

    private int setRollType(IInputHandler inputHandler) {
        System.out.println("Choose a roll type:");
        System.out.println("1. Roll with Advantage");
        System.out.println("2. Roll with Disadvantage");
        System.out.println("0. Normal Roll");
        int rollType = inputHandler.getIntegerInput("Enter your choice:", new NumberValidator(0, 2));
        return rollType;
    }

    private int Roll() {
        Random random = new Random();
        return random.nextInt(20) + 1; // Roll a d20 (1-20)
    }

    public int roll_dice() {
        int result = 0;
        for (int i = 0; i < 10; i++) {
            result += Roll();    
        }

        return result / 10; 
    }

    public int roll_diceWithAdvantage() {
        int firstRoll = roll_dice();
        int secondRoll = roll_dice();
        return Math.max(firstRoll, secondRoll); // Return the higher of the two rolls
    }

    public int rollWithDisadvantage() {
        int firstRoll = roll_dice();
        int secondRoll = roll_dice();
        return Math.min(firstRoll, secondRoll); // Return the lower of the two rolls
    }

    public int roll(IInputHandler inputHandler) {
        int rollType = setRollType(inputHandler);

        switch (rollType) {
            case ROLL_WITH_ADVANTAGE:
                return roll_diceWithAdvantage();
            case ROLL_WITH_DISADVANTAGE:
                return rollWithDisadvantage();
            case ROLL_NORMAL:
            default:
                return roll_dice();
        }
    }
}

