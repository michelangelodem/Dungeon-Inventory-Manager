package User;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String username;
    private String passwordHash;
    private String salt;
    private String email;
    private long createdDate;
    
    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        this.createdDate = System.currentTimeMillis();
        this.salt = generateSalt();
        this.passwordHash = hashPassword(password, salt);
    }
    
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public boolean verifyPassword(String password) {
        String hashedInput = hashPassword(password, salt);
        return hashedInput.equals(passwordHash);
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public long getCreatedDate() {
        return createdDate;
    }
    
    public String getInventoryFileName() {
        return "inventory_" + username + ".dat";
    }
}