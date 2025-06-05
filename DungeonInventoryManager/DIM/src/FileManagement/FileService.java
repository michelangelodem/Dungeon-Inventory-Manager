package FileManagement;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Items.*;

import java.io.File;

public class FileService implements IFileService {
    private final File file;
    private final String fileName;
    
    public FileService(String fileName) {
        this.fileName = fileName;
        this.file = new File(fileName);
    }
    
    @Override
    public String getFileName() {
        return fileName;
    }
    
    @Override
    public void prepareFileForWrite() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            writer.write("");
        } catch (IOException e) {
            System.out.println("Error preparing file for writing: " + e.getMessage());
        }
    }
    
    @Override
    public boolean writeItems(Item[] items) {
        prepareFileForWrite();
        try {
            for (Item item : items) {
                writeItemToFile(item);
            }
            return file.exists();
        } catch (Exception e) {
            System.out.println("Error writing items: " + e.getMessage());
            return false;
        }
    }
    
    private void writeItemToFile(Item item) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(item.getName() + "\n" + 
                        item.getDescription() + "\n" + 
                        item.getPrice() + "\n" + 
                        item.getWeight() + "\n\t");

            if (item instanceof Weapon) {
                Weapon sword = (Weapon) item;
                writer.write(sword.getDamage() + "\n");
            }
            else if (item instanceof Armor) {
                Armor armor = (Armor) item;
                writer.write(armor.getArmorClass() + "\n");
            }
            writer.write("\t");
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    
    @Override
    public List<Item> readItems() {
        List<Item> items = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (true) {
                Item item = readSingleItem(reader);
                if (item.getName() == null) {
                    break;
                }
                items.add(item);
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        
        return items;
    }
    
    private Item readSingleItem(BufferedReader reader) {
        StringBuilder data = new StringBuilder();
        Item item = new Item();
        
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    break;
                }
                data.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
        
        if (data.length() > 0) {
            String[] itemData = data.toString().split("\n");
            if (itemData.length > 0) {
                String itemType = itemData[0];
                
                // Create appropriate item type based on first line
                if ("Sword".equals(itemType) && itemData.length >= 6) {
                    item = new Weapon();
                    // Skip the type line and pass the rest
                    String[] actualData = new String[itemData.length - 1];
                    System.arraycopy(itemData, 1, actualData, 0, actualData.length);
                    item.fromStr2Item(actualData);
                } else {
                    // Default to regular Item
                    item = new Item();
                    String[] actualData = new String[itemData.length - 1];
                    System.arraycopy(itemData, 1, actualData, 0, actualData.length);
                    item.fromStr2Item(actualData);
                }
            }
        }
        
        if (item == null) {
            item = new Item(); // Return empty item if parsing failed
        }
        
        return item;
    }
}