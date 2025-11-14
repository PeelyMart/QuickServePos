package UserInterface;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class dashboardUI {

    @FXML
    private BorderPane mainContent;


    @FXML
    private void handleOptions(ActionEvent event) throws IOException {
        loadContent("/Resources/MainMenu/options.fxml");
    }

    @FXML
    private void handleReports(ActionEvent event) throws IOException {
        loadContent("/Resources/MainMenu/reports.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
    }

    @FXML
    private void handleTransactions(ActionEvent event) throws IOException {
        loadContent("/Resources/MainMenu/transactions.fxml"); // or your logout screen
    }

    /**
     * Utility to load FXML into the center of mainContent
     */
    private void loadContent(String fxmlFile) throws IOException {
        Parent view = FXMLLoader.load(getClass().getResource(fxmlFile));
        mainContent.setCenter(view);
    }
}
