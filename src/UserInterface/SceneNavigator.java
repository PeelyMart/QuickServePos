package UserInterface;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SceneNavigator {
    public static void switchScene(Button sourceButton, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            AnchorPane root = loader.load();
            Stage stage = (Stage) sourceButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void testClick(String buttonName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Button Test");
        alert.setHeaderText(null);
        alert.setContentText(buttonName + " clicked!");
        alert.showAndWait();
    }

    public static void showError(String msg) {
        showAlert(Alert.AlertType.ERROR, "Error", msg);
    }

    public static void showWarning(String msg) {
        showAlert(Alert.AlertType.WARNING, "Warning", msg);
    }

    public static void showInfo(String msg) {
        showAlert(Alert.AlertType.INFORMATION, "Info", msg);
    }

    public static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void switchNoButton(Stage stage, String fxmlPath, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Get controller AFTER load() (initialize() is called during load())
            Object controller = loader.getController();
            System.out.println("SceneNavigator: Controller retrieved: " + (controller != null ? controller.getClass().getName() : "null"));
            System.out.println("SceneNavigator: Data to pass: " + (data != null ? data.getClass().getName() : "null"));

            // If controller has setData(), call it BEFORE setting scene
            if (controller != null && data != null) {
                try {
                    System.out.println("SceneNavigator: Attempting to call setData()...");
                    controller.getClass().getMethod("setData", Object.class).invoke(controller, data);
                    System.out.println("SceneNavigator: setData() called successfully");
                } catch (NoSuchMethodException ignored) {
                    System.out.println("SceneNavigator: Controller doesn't have setData() method");
                    // controller doesn't have setData → ignore
                } catch (Exception e) {
                    System.err.println("SceneNavigator: Error calling setData(): " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("SceneNavigator: Skipping setData() - controller: " + (controller != null) + ", data: " + (data != null));
            }

            // Switch scene AFTER setData() is called
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            System.out.println("SceneNavigator: Scene set and shown");

        } catch (Exception e) {
            System.err.println("SceneNavigator: Error in switchNoButton: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
