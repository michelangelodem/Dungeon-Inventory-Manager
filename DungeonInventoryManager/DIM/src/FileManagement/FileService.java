package FileManagement;

import Items.Item;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileService implements IFileService {

    private static final String FILE_PATH = "inventory.dat";

    @Override
    public void writeItemsToFile(List<Item> items, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(items);
            System.out.println("Inventory saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving inventory: " + e.getMessage());
        }
    }

    @Override
    public List<Item> readItemsFromFile(String filename) {
        List<Item> items = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            items = (List<Item>) ois.readObject();
            System.out.println("Inventory loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("No existing inventory found. Starting with an empty inventory.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading inventory: " + e.getMessage());
        }
        return items;
    }
}

