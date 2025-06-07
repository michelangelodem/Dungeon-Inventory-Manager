package Items;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class Item {
    private String name;
    private String description;
    private double price;
    private double weight;
    private int quantity = 0;

    public Item() { }

    public Item(String name, String description, double price, double weight) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.weight = weight;
        this.quantity = 1; // Fixed: should be 1, not ++
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public double getWeight() {
        return weight;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setters with validation
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (description.length() > 100) {
            throw new IllegalArgumentException("Description cannot exceed 100 characters");
        }
        this.description = description.trim();
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        this.weight = weight;
    }

    // Quantity management methods
    public void increaseQuantity() {
        this.quantity++;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
        } else {
            System.out.println("Quantity cannot be less than 0.");
        }
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = quantity;
    }

    // Display method
    public void PrintItem() {
        System.out.println("Name: " + this.name);
        System.out.println("Description: " + this.description);
        System.out.println("Price: $" + String.format("%.2f", this.price));
        System.out.println("Weight: " + this.weight + " kg");
        System.out.println("Quantity: " + this.quantity);
    }

    // Input method with validation
    public void readItem(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Enter item name: ");
                String inputName = scanner.nextLine();
                setName(inputName);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter item description: ");
                String inputDescription = scanner.nextLine();
                setDescription(inputDescription);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage() + " Please try again.");
            }
        }

        while (true) {
            try {
                System.out.print("Enter item price: ");
                double inputPrice = scanner.nextDouble();
                setPrice(inputPrice);
                break;
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid price. Please try again.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        while (true) {
            try {
                System.out.print("Enter item weight: ");
                double inputWeight = scanner.nextDouble();
                setWeight(inputWeight);
                scanner.nextLine(); // Consume leftover newline
                break;
            } catch (Exception e) {
                System.out.println("Error: Please enter a valid weight. Please try again.");
                scanner.nextLine(); // Clear invalid input
            }
        }

        this.quantity = 1; // Set initial quantity
    }

    // Search method
    public boolean compareName(String name) {
        if (this.name == null || name == null) {
            return false;
        }
        return this.name.toLowerCase().contains(name.toLowerCase());
    }

    // File parsing method with validation
    public void fromStr2Item(String[] itemData) {
        if (itemData.length < 4) {
            throw new IllegalArgumentException("Invalid item data: insufficient fields");
        }
        
        try {
            setName(itemData[0]);
            setDescription(itemData[1]);
            setPrice(Double.parseDouble(itemData[2]));
            setWeight(Double.parseDouble(itemData[3]));
            this.quantity = 1; // Default quantity when loading from file
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid item data: price or weight is not a valid number");
        }
    }

    @Override
    public String toString() {
        return String.format("Item{name='%s', description='%s', price=%.2f, weight=%.2f, quantity=%d}",
                name, description, price, weight, quantity);
    }

    public String[] loadItemData(BufferedReader reader) {
        String name = null;
        String description = null;
        String priceStr = null;
        String weightStr = null;

        try {
            name = reader.readLine();
            description = reader.readLine();
            priceStr = reader.readLine();
            weightStr = reader.readLine();
        } catch (IOException e) {
            System.out.println("Error reading item data: " + e.getMessage());
            return null;
        }
            String[] itemData = new String[4];
            // Check if any required field is null
            if (name == null || description == null || priceStr == null || weightStr == null) {
                System.out.println("Warning: Incomplete item data found in file");
                return null;
            }
            
            // Trim all strings
            itemData[0] = name.trim();
            itemData[1] = description.trim();
            itemData[2] = priceStr.trim();
            itemData[3] = weightStr.trim();

            return itemData;
    }

}