package Commands;
public interface ICommand {
    void execute();
    String getDescription();
    int getCommandId();
}