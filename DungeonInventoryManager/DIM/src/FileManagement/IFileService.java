package FileManagement;
import java.util.List;

import Items.Item;

public interface IFileService {
    List<Item> readItems();
    boolean writeItems(Item[] items);
    String getFileName();
    void prepareFileForWrite();
}