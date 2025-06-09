package InputValidation;


public class DamageValidator implements IInputValidator {
    private String damage;

    public DamageValidator(String damage) {
        this.damage = damage;
    }

    @Override
    public void setInput(String damage) {
        this.damage = damage;
    }

    @Override
    public String getInput() {
        return damage;
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
    
    @Override
    public boolean isValid() {
        return checkIfValidDamageString();
    }

    private boolean checkIfValidDamageString() {
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
    public String getErrorMessage() {
        return "Invalid damage format. Please enter in the form 'XdY' or 'XdY + XdY', where X and Y are integers.";
    }
}
