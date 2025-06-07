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
            // Write item type first
            if (item instanceof Weapon) {
                writer.write("WEAPON\n");
            } else if (item instanceof Armor) {
                writer.write("ARMOR\n");
            } else {
                writer.write("ITEM\n");
            }
            
            // Write basic item data
            writer.write(item.getName() + "\n");
            writer.write(item.getDescription() + "\n");
            writer.write(String.valueOf(item.getPrice()) + "\n");
            writer.write(String.valueOf(item.getWeight()) + "\n");

            // Write specific data based on item type
            if (item instanceof Weapon) {
                Weapon weapon = (Weapon) item;
                writer.write(weapon.getDamage() + "\n");
            } else if (item instanceof Armor) {
                Armor armor = (Armor) item;
                writer.write(String.valueOf(armor.getArmorClass()) + "\n");
            }
            
            // Write separator
            writer.write("---\n");
            
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
        String itemType;
        String[] itemData;
        String seperator = null;
        itemType = loadItemType(reader);
            
        switch (itemType) {
            case "WEAPON":
                Weapon weapon = new Weapon();
                itemData = weapon.loadItemData(reader);
                catchSeparator(reader);
                weapon.fromStr2Item(itemData);
                return weapon;
            case "ARMOR":
                Armor armor = new Armor();
                itemData = armor.loadItemData(reader);
                catchSeparator(reader);
                armor.fromStr2Item(itemData);
                return armor;
            default:
                Item item = new Item();
                itemData = item.loadItemData(reader);
                catchSeparator(reader);
                item.fromStr2Item(itemData);
                return item;   
            }
    }

    private String loadItemType(BufferedReader reader) {
        try {
            String itemType = reader.readLine();
            if (itemType == null) {
                return null;
            } 
            return itemType.trim().toUpperCase();
        } catch (IOException e) {
            System.out.println("Error reading item type: " + e.getMessage());
        } 
        return null;
    }

    private void catchSeparator(BufferedReader reader) {
        try {
            String separator = reader.readLine();
            if (separator == null || !separator.equals("---")) {
                System.out.println("Warning: Expected separator '---' not found");
            }
        } catch (IOException e) {
            System.out.println("Error reading separator: " + e.getMessage());
        }
    }

}