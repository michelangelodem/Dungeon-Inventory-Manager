package View;

import User.User;
import User.UserManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.function.Consumer;

public class AuthView {
    private Stage authStage;
    private Consumer<User> onSuccessfulAuth;
    private UserManager userManager;
    private boolean isLoginMode = true;
    
    public AuthView(Stage primaryStage, Consumer<User> onSuccessfulAuth) {
        this.authStage = primaryStage;
        this.onSuccessfulAuth = onSuccessfulAuth;
        this.userManager = new UserManager();
        createAuthScene();
    }
    
    private void createAuthScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // Title
        Label titleLabel = new Label("Dungeon Inventory Manager");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        // Mode indicator
        Label modeLabel = new Label("Login to Your Account");
        modeLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #3498db; -fx-font-weight: bold;");
        
        // Username field
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        usernameField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        
        // Password field
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        passwordField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        
        // Email field (only visible in register mode)
        TextField emailField = new TextField();
        emailField.setPromptText("Email Address");
        emailField.setMaxWidth(300);
        emailField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        emailField.setVisible(false);
        emailField.setManaged(false);
        
        // Password requirements label (only visible in register mode)
        Label passwordReqLabel = new Label("Password must be at least 6 characters long");
        passwordReqLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");
        passwordReqLabel.setVisible(false);
        passwordReqLabel.setManaged(false);
        
        // Main action button
        Button actionButton = new Button("Login");
        actionButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        actionButton.setOnMouseEntered(e -> actionButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        actionButton.setOnMouseExited(e -> actionButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        
        // Toggle mode button
        Button toggleButton = new Button("Don't have an account? Register");
        toggleButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: transparent; -fx-text-fill: #3498db; -fx-border-color: #3498db; -fx-border-radius: 3px; -fx-background-radius: 3px;");
        toggleButton.setOnMouseEntered(e -> toggleButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-color: #3498db; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        toggleButton.setOnMouseExited(e -> toggleButton.setStyle("-fx-font-size: 12px; -fx-padding: 5px 10px; -fx-background-color: transparent; -fx-text-fill: #3498db; -fx-border-color: #3498db; -fx-border-radius: 3px; -fx-background-radius: 3px;"));
        
        // Set button actions
        actionButton.setOnAction(e -> {
            if (isLoginMode) {
                handleLogin(usernameField.getText(), passwordField.getText());
            } else {
                handleRegister(usernameField.getText(), passwordField.getText(), emailField.getText());
            }
        });
        
        toggleButton.setOnAction(e -> {
            isLoginMode = !isLoginMode;
            if (isLoginMode) {
                modeLabel.setText("Login to Your Account");
                actionButton.setText("Login");
                toggleButton.setText("Don't have an account? Register");
                emailField.setVisible(false);
                emailField.setManaged(false);
                passwordReqLabel.setVisible(false);
                passwordReqLabel.setManaged(false);
                authStage.setHeight(400);
            } else {
                modeLabel.setText("Create New Account");
                actionButton.setText("Register");
                toggleButton.setText("Already have an account? Login");
                emailField.setVisible(true);
                emailField.setManaged(true);
                passwordReqLabel.setVisible(true);
                passwordReqLabel.setManaged(true);
                authStage.setHeight(500);
            }
            authStage.centerOnScreen();
        });
        
        // Allow Enter key to trigger action
        passwordField.setOnAction(e -> actionButton.fire());
        emailField.setOnAction(e -> actionButton.fire());
        
        // Add all elements to the layout
        root.getChildren().addAll(
            titleLabel, 
            modeLabel, 
            usernameField, 
            passwordField, 
            emailField, 
            passwordReqLabel, 
            actionButton, 
            toggleButton
        );
        
        // Create and set the scene
        Scene scene = new Scene(root, 400, 400);
        authStage.setTitle("Authentication - Dungeon Inventory Manager");
        authStage.setScene(scene);
        authStage.setResizable(false);
        authStage.centerOnScreen();
    }
    
    private void handleLogin(String username, String password) {
        if (username.trim().isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        
        User user = userManager.authenticateUser(username, password);
        if (user != null) {
            onSuccessfulAuth.accept(user);
        } else {
            showError("Invalid username or password.");
        }
    }
    
    private void handleRegister(String username, String password, String email) {
        if (username.trim().isEmpty() || password.isEmpty() || email.trim().isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }
        
        if (userManager.userExists(username)) {
            showError("Username already exists. Please choose a different username.");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long.");
            return;
        }
        
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address.");
            return;
        }
        
        if (userManager.registerUser(username, password, email)) {
            showSuccess("Account created successfully! You can now login.");
            // Switch to login mode
            isLoginMode = true;
            createAuthScene(); // Recreate the scene in login mode
        } else {
            showError("Registration failed. Please try again.");
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void show() {
        authStage.show();
    }
}