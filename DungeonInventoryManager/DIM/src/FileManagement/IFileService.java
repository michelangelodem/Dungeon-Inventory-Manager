package FileManagement;

import java.util.List;
import Items.Item;

public interface IFileService {
    void writeItemsToFile(List<Item> items, String filename);
    List<Item> readItemsFromFile(String filename);
}

