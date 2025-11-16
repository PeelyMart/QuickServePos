package UserInterface;

import DAO.MenuItemDAO;
import Model.MenuItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class MenuOptionsUI {

    @FXML
    private Button addMenuButton, searchMenuButton, updateMenuButton, removeMenuButton, backButton;

    @FXML
    private TextField searchMenu;

    @FXML
    private final MenuItemDAO menuItemDAO = new MenuItemDAO();

    @FXML
    private void initialize() {
        addMenuButton.setOnAction(e -> handleAdd());
        searchMenuButton.setOnAction(e -> handleSearch());
        updateMenuButton.setOnAction(e -> handleUpdate());
        removeMenuButton.setOnAction(e -> handleDelete());

        if (backButton != null) {
            backButton.setOnAction(e ->
                    SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml"));
        }
    }

    // ===================== ADD =====================
    @FXML
    private void handleAdd() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Menu Item");
        nameDialog.setHeaderText("Enter Menu Item Name:");
        Optional<String> nameInput = nameDialog.showAndWait();

        nameInput.ifPresent(name -> {
            TextInputDialog descDialog = new TextInputDialog();
            descDialog.setTitle("Add Menu Item");
            descDialog.setHeaderText("Enter Menu Description:");
            Optional<String> descInput = descDialog.showAndWait();

            descInput.ifPresent(desc -> {
                TextInputDialog priceDialog = new TextInputDialog();
                priceDialog.setTitle("Add Menu Item");
                priceDialog.setHeaderText("Enter Menu Price:");
                Optional<String> priceInput = priceDialog.showAndWait();

                priceInput.ifPresent(priceStr -> {
                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ex) {
                        SceneNavigator.showError("Price must be a number.");
                        return;
                    }

                    MenuItem m = new MenuItem();
                    m.setMenuName(name);
                    m.setDescription(desc);
                    m.setPrice(price);
                    m.setStatus(true); // default available

                    if (menuItemDAO.addMenuItem(m)) {
                        SceneNavigator.showInfo(
                                "Menu Item added successfully!\n" +
                                        "ID: " + m.getMenuId() + "\n" +
                                        "Name: " + m.getMenuName() + "\n" +
                                        "Description: " + m.getDescription() + "\n" +
                                        "Price: " + m.getPrice()
                        );
                    } else {
                        SceneNavigator.showError("Failed to add Menu Item. Try again.");
                    }
                });
            });
        });
    }

    // ===================== SEARCH =====================
    @FXML
    private void handleSearch() {
        String input = searchMenu.getText();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            SceneNavigator.showError("Enter a valid numeric Menu ID.");
            return;
        }

        MenuItem m = menuItemDAO.getMenuItemById(id);
        if (m != null) {
            SceneNavigator.showInfo(
                    "Menu Item Found:\n" +
                            "ID: " + m.getMenuId() + "\n" +
                            "Name: " + m.getMenuName() + "\n" +
                            "Description: " + m.getDescription() + "\n" +
                            "Price: " + m.getPrice() + "\n" +
                            "Status: " + (m.getStatus() ? "Available" : "Unavailable")
            );
        } else {
            SceneNavigator.showError("No Menu Item found with ID: " + id);
        }
    }

    // ===================== UPDATE =====================
    @FXML
    private void handleUpdate() {
        String input = searchMenu.getText();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            SceneNavigator.showError("Enter a valid numeric Menu ID to update.");
            return;
        }

        MenuItem m = menuItemDAO.getMenuItemById(id);
        if (m == null) {
            SceneNavigator.showError("Menu Item not found.");
            return;
        }

        TextInputDialog nameDialog = new TextInputDialog(m.getMenuName());
        nameDialog.setTitle("Update Menu Item");
        nameDialog.setHeaderText("Enter new Menu Name:");
        Optional<String> nameInput = nameDialog.showAndWait();

        nameInput.ifPresent(newName -> {
            TextInputDialog descDialog = new TextInputDialog(m.getDescription());
            descDialog.setTitle("Update Menu Item");
            descDialog.setHeaderText("Enter new Menu Description:");
            Optional<String> descInput = descDialog.showAndWait();

            descInput.ifPresent(newDesc -> {
                TextInputDialog priceDialog = new TextInputDialog(m.getPrice().toString());
                priceDialog.setTitle("Update Menu Item");
                priceDialog.setHeaderText("Enter new Menu Price:");
                Optional<String> priceInput = priceDialog.showAndWait();

                priceInput.ifPresent(priceStr -> {
                    double price;
                    try {
                        price = Double.parseDouble(priceStr);
                    } catch (NumberFormatException ex) {
                        SceneNavigator.showError("Price must be a number.");
                        return;
                    }

                    m.setMenuName(newName);
                    m.setDescription(newDesc);
                    m.setPrice(price);

                    if (menuItemDAO.updateMenuItem(m)) {
                        SceneNavigator.showInfo("Menu Item " + id + " updated successfully.");
                    } else {
                        SceneNavigator.showError("Update failed.");
                    }
                });
            });
        });
    }

    // ===================== DELETE =====================
    @FXML
    private void handleDelete() {
        String input = searchMenu.getText();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            SceneNavigator.showError("Enter a valid numeric Menu ID to delete.");
            return;
        }

        if (menuItemDAO.deleteMenuItem(id)) {
            SceneNavigator.showInfo("Menu Item " + id + " deleted successfully.");
        } else {
            SceneNavigator.showError("Deletion failed. Menu Item may not exist.");
        }
    }
}
