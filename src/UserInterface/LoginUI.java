package UserInterface;

import Controller.SessionTracker;
import Controller.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginUI {

    @FXML
    private TextField staffIDField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private Stage loginStage;
    private Stage mainStage;

    public static void openLogin(Stage mainStage) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginUI.class.getResource("/Resources/LogIn/login.fxml"));
            AnchorPane root = loader.load();

            LoginUI controller = loader.getController();
            
            // If mainStage is provided, use it; otherwise create a new one
            if (mainStage != null) {
                controller.mainStage = mainStage;
                // Use the existing stage (switch scene)
                mainStage.setScene(new Scene(root));
                mainStage.setTitle("Login");
                mainStage.show();
            } else {
                // Create a new stage for login
                controller.mainStage = new Stage();
                Stage loginStage = new Stage();
                controller.loginStage = loginStage;
                loginStage.setScene(new Scene(root));
                loginStage.setTitle("Login");
                loginStage.initModality(Modality.APPLICATION_MODAL); // modal popup
                loginStage.setResizable(false);
                loginStage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
    }

    public void setStages(Stage loginStage, Stage mainStage) {
        this.loginStage = loginStage;
        this.mainStage = mainStage;
    }

    @FXML
    private void handleLogin() {
        int resultFlag = UserService.logIn(
                stringHelper(staffIDField.getText()),
                stringHelper(passwordField.getText())
        );

        if (resultFlag == 0) {
            SessionTracker.startSession(UserService.getCurrentUser());
            // Login successful
            if (loginStage != null) loginStage.close();
            DashboardUI.openDashboard(mainStage);
        } else if (resultFlag == 1) {
            // Incorrect username/user cannot be found
            SceneNavigator.showError("Incorrect username or user cannot be found.");
        } else if (resultFlag == 2) {
            // Incorrect password
            SceneNavigator.showError("Incorrect password.");
        } else if (resultFlag == 999) {
            // Input error
            SceneNavigator.showError("Please enter valid Staff ID and Password (numbers only).");
        } else {
            SceneNavigator.showError("Login failed. Please try again.");
        }
    }

    private int stringHelper(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

