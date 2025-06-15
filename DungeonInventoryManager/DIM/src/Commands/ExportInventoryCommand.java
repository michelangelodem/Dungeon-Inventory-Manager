package Commands;

import Inventory.InventoryService;
import Items.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportInventoryCommand implements ICommand {
    private InventoryService is;

    private final String TXT_FILENAME = "items.txt";

    public ExportInventoryCommand(InventoryService is){
        this.is = is;
    }

    public void prepareFileForWrite() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TXT_FILENAME, false))) {
            writer.write("");
        } catch (IOException e) {
            System.out.println("Error preparing file for writing: " + e.getMessage());
        }
    }

    @Override
    public void execute() {
        prepareFileForWrite();
        List<Item> items = is.getAllItems();
        try {
            for (Item item : items) {
                writeItemToFile(item);
            }
            System.out.println("Items copied to:" + TXT_FILENAME);
        } catch (Exception e) {
            System.out.println("Error writing items: " + e.getMessage());
        }
    }

    private void writeItemToFile(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TXT_FILENAME, true))) {   
            writer.write("--- " + item.getClass().getSimpleName() + " ---\n"); 
            item.writeToStream(writer); 
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
