package UserInterface;

import DAO.MenuItemDAO;
import Model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuOptionsUI {

    @FXML
    private Button addMenuButton, searchMenuButton, updateMenuButton, removeMenuButton, backButton;

    @FXML
    private TextField searchMenu;

    @FXML
    private TableView<MenuItem> menuTable;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> descriptionColumn;

    @FXML
    private TableColumn<MenuItem, Double> priceColumn;

    @FXML
    private TableColumn<MenuItem, Boolean> statusColumn;

    private final MenuItemDAO menuItemDAO = new MenuItemDAO();

    @FXML
    private void initialize() {
        setupTableView();
        loadAllMenuItems();

        addMenuButton.setOnAction(e -> handleAdd());
        searchMenuButton.setOnAction(e -> handleSearch());
        updateMenuButton.setOnAction(e -> handleUpdate());
        removeMenuButton.setOnAction(e -> handleDelete());

        if (backButton != null) {
            backButton.setOnAction(e ->
                    SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml"));
        }

        // Allow Enter key in search field
        if (searchMenu != null) {
            searchMenu.setOnAction(e -> handleSearch());
        }
    }

    private void setupTableView() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("menuId"));
        }
        if (nameColumn != null) {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("menuName"));
        }
        if (descriptionColumn != null) {
            descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        }
        if (priceColumn != null) {
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceColumn.setCellFactory(column -> new TableCell<MenuItem, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", price));
                    }
                }
            });
        }
        if (statusColumn != null) {
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusColumn.setCellFactory(column -> new TableCell<MenuItem, Boolean>() {
                @Override
                protected void updateItem(Boolean status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty || status == null) {
                        setText(null);
                    } else {
                        setText(status ? "Available" : "Unavailable");
                    }
                }
            });
        }
    }

    private void loadAllMenuItems() {
        List<MenuItem> allItems = menuItemDAO.getAllMenuItems();
        if (menuTable != null && allItems != null) {
            menuTable.setItems(FXCollections.observableArrayList(allItems));
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
                    try {
                        BigDecimal price = new BigDecimal(priceStr);

                        MenuItem m = new MenuItem();
                        m.setMenuName(name);
                        m.setDescription(desc);
                        m.setPrice(price.doubleValue());
                        m.setStatus(true); // default available

                        if (menuItemDAO.addMenuItem(m)) {
                            SceneNavigator.showInfo("Menu Item added successfully!");
                            loadAllMenuItems(); // Refresh table
                        } else {
                            SceneNavigator.showError("Failed to add Menu Item. Try again.");
                        }
                    } catch (NumberFormatException ex) {
                        SceneNavigator.showError("Price must be a valid number.");
                    }
                });
            });
        });
    }

    // ===================== SEARCH =====================
    @FXML
    private void handleSearch() {
        String input = searchMenu.getText().trim();
        if (input.isEmpty()) {
            loadAllMenuItems(); // Show all if search is empty
            return;
        }

        // Try parsing as ID first
        try {
            int id = Integer.parseInt(input);
            MenuItem m = menuItemDAO.getMenuItemById(id);
            if (m != null) {
                menuTable.setItems(FXCollections.observableArrayList(m));
            } else {
                SceneNavigator.showError("No Menu Item found with ID: " + id);
                menuTable.getItems().clear();
            }
            return;
        } catch (NumberFormatException ignored) {
            // Not a number, search by name
        }

        // Search by name (partial match)
        List<MenuItem> allItems = menuItemDAO.getAllMenuItems();
        if (allItems != null) {
            List<MenuItem> matching = allItems.stream()
                    .filter(item -> item.getMenuName() != null &&
                            item.getMenuName().toLowerCase().contains(input.toLowerCase()))
                    .collect(Collectors.toList());

            if (!matching.isEmpty()) {
                menuTable.setItems(FXCollections.observableArrayList(matching));
            } else {
                SceneNavigator.showWarning("No menu items found matching: " + input);
                menuTable.getItems().clear();
            }
        }
    }

    // ===================== UPDATE =====================
    @FXML
    private void handleUpdate() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a menu item from the table to update.");
            return;
        }

        // Reload from database to get latest data
        MenuItem m = menuItemDAO.getMenuItemById(selected.getMenuId());
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
                    try {
                        BigDecimal price = new BigDecimal(priceStr);

                        // Toggle status
                        ChoiceDialog<String> statusDialog = new ChoiceDialog<>(
                                m.getStatus() ? "Available" : "Unavailable",
                                "Available", "Unavailable");
                        statusDialog.setTitle("Update Menu Item");
                        statusDialog.setHeaderText("Select Status:");
                        Optional<String> statusInput = statusDialog.showAndWait();

                        statusInput.ifPresent(statusStr -> {
                            m.setMenuName(newName);
                            m.setDescription(newDesc);
                            m.setPrice(price.doubleValue());
                            m.setStatus("Available".equals(statusStr));

                            if (menuItemDAO.updateMenuItem(m)) {
                                SceneNavigator.showInfo("Menu Item updated successfully.");
                                loadAllMenuItems(); // Refresh table
                            } else {
                                SceneNavigator.showError("Update failed.");
                            }
                        });
                    } catch (NumberFormatException ex) {
                        SceneNavigator.showError("Price must be a valid number.");
                    }
                });
            });
        });
    }

    // ===================== DELETE =====================
    @FXML
    private void handleDelete() {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a menu item from the table to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Menu Item");
        confirmAlert.setHeaderText("Are you sure you want to delete this menu item?");
        confirmAlert.setContentText("ID: " + selected.getMenuId() + "\nName: " + selected.getMenuName());

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        confirmAlert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            if (menuItemDAO.deleteMenuItem(selected.getMenuId())) {
                SceneNavigator.showInfo("Menu Item deleted successfully.");
                loadAllMenuItems(); // Refresh table
            } else {
                SceneNavigator.showError("Deletion failed. Menu Item may not exist.");
            }
        }
    }
}