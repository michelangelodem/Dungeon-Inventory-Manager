package Items;

import InputValidation.IInputHandler;

public interface IItemFactory {
    Item createItem(String itemType, String name, String description, double price, double weight, IInputHandler inputHandler);
}

