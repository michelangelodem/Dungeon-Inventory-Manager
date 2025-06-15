package Commands;

public class ExitCommand implements ICommand {
    @Override
    public void execute() {
        System.out.println("Exiting Dungeon Inventory Manager. Goodbye!");
        System.exit(0);
    }
}

