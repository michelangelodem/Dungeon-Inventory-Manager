package User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE = "users.dat";
    private Map<String, User> users;
    
    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }
    
    public boolean registerUser(String username, String password, String email) {
        if (users.containsKey(username.toLowerCase())) {
            return false; // Username already exists
        }
        
        if (!isValidEmail(email)) {
            return false; // Invalid email format
        }
        
        if (!isValidPassword(password)) {
            return false; // Password doesn't meet requirements
        }
        
        User newUser = new User(username, password, email);
        users.put(username.toLowerCase(), newUser);
        saveUsers();
        return true;
    }
    
    public User authenticateUser(String username, String password) {
        User user = users.get(username.toLowerCase());
        if (user != null && user.verifyPassword(password)) {
            return user;
        }
        return null;
    }
    
    public boolean userExists(String username) {
        return users.containsKey(username.toLowerCase());
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
    
    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    private void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File doesn't exist yet, start with empty users map
            users = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            users = new HashMap<>();
        }
    }
    
    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    public int getUserCount() {
        return users.size();
    }
}