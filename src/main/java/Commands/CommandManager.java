package Commands;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, ICommand> commands = new HashMap<>();

    public void addCommand(String commandName, ICommand command) {
        commands.put(commandName, command);
    }

    public void executeCommand(String commandName) {
        ICommand command = commands.get(commandName);
        if (command != null) {
            command.execute();
        } else {
            System.out.println("Unknown command: " + commandName);
        }
    }
}

