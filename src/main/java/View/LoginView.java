package View;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private Stage loginStage;
    private Runnable onSuccessfulLogin;
    private static final String REGISTER_CODE = "DUNGEON2024"; // You can change this to your preferred code
    
    public LoginView(Stage primaryStage, Runnable onSuccessfulLogin) {
        this.loginStage = primaryStage;
        this.onSuccessfulLogin = onSuccessfulLogin;
        createLoginScene();
    }
    
    private void createLoginScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: #2c3e50;");
        
        // Title
        Label titleLabel = new Label("Dungeon Inventory Manager");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #ecf0f1;");
        
        // Subtitle
        Label subtitleLabel = new Label("Enter Register Code to Access");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #bdc3c7;");
        
        // Password field for register code
        PasswordField codeField = new PasswordField();
        codeField.setPromptText("Enter register code");
        codeField.setMaxWidth(300);
        codeField.setStyle("-fx-font-size: 14px; -fx-padding: 10px;");
        
        // Login button
        Button loginButton = new Button("Access Inventory");
        loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #2980b9; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-font-size: 14px; -fx-padding: 10px 20px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-border-radius: 5px; -fx-background-radius: 5px;"));
        
        // Set login button action
        loginButton.setOnAction(e -> handleLogin(codeField.getText()));
        
        // Allow Enter key to trigger login
        codeField.setOnAction(e -> handleLogin(codeField.getText()));
        
        // Add all elements to the layout
        root.getChildren().addAll(titleLabel, subtitleLabel, codeField, loginButton);
        
        // Create and set the scene
        Scene scene = new Scene(root, 400, 300);
        loginStage.setTitle("Login - Dungeon Inventory Manager");
        loginStage.setScene(scene);
        loginStage.setResizable(false);
        loginStage.centerOnScreen();
    }
    
    private void handleLogin(String enteredCode) {
        if (enteredCode != null && enteredCode.equals(REGISTER_CODE)) {
            // Successful login - run the callback to show main application
            onSuccessfulLogin.run();
        } else {
            // Show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Access Denied");
            alert.setHeaderText("Invalid Register Code");
            alert.setContentText("The register code you entered is incorrect. Please try again.");
            alert.showAndWait();
        }
    }
    
    public void show() {
        loginStage.show();
    }
}