package DnDMechanics;

import java.util.Random;

public class RollD20 {
    public int roll() {
        Random random = new Random();
        return random.nextInt(20) + 1; // Roll a d20 (1-20)
    }
}

