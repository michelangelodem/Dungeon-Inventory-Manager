package DnDMechanics;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DamageCalculator {

    private static final Pattern DAMAGE_PATTERN = Pattern.compile("(\\d+)d(\\d+)(?:\\s*([+\\-])\\s*(\\d+))?");
    private Random random;

    public DamageCalculator() {
        this.random = new Random();
    }

    public int calculateDamage(String damageRoll) {
        Matcher matcher = DAMAGE_PATTERN.matcher(damageRoll.toLowerCase());
        if (!matcher.matches()) {
            System.err.println("Invalid damage roll format: " + damageRoll);
            return 0;
        }

        int numDice = Integer.parseInt(matcher.group(1));
        int dieType = Integer.parseInt(matcher.group(2));
        int modifier = 0;

        if (matcher.group(3) != null && matcher.group(4) != null) {
            String operator = matcher.group(3);
            int modValue = Integer.parseInt(matcher.group(4));
            if (operator.equals("+")) {
                modifier = modValue;
            } else if (operator.equals("-")) {
                modifier = -modValue;
            }
        }

        int totalDamage = 0;
        for (int i = 0; i < numDice; i++) {
            totalDamage += random.nextInt(dieType) + 1;
        }

        return totalDamage + modifier;
    }
}

