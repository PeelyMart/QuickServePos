package UserInterface;

import DAO.MenuItemDAO;
import DAO.OrderDB;
import DAO.TableDAO;
import Model.MenuItem;
import Model.Order;
import Model.OrderItem;
import Model.OrderStatus;
import Model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import Model.Reservations;

public class TransactionMenuUI {

    // Buttons
    @FXML private Button ordersButton, reservationsButton, backButton;

    // Orders Table
    @FXML private TableView<OrderDisplay> ordersTable;
    @FXML private TableColumn<OrderDisplay, String> menuItemColumn;
    @FXML private TableColumn<OrderDisplay, String> quantityColumn;

    // Reservations Table
    @FXML private TableView<ReservationDisplay> reservationsTable;
    @FXML private TableColumn<ReservationDisplay, String> reservationDateColumn;
    @FXML private TableColumn<ReservationDisplay, String> reservationNameColumn;

    // Table buttons container (will be added to FXML)
    @FXML private FlowPane tableButtonsPane;
    
    // Table number display
    @FXML private javafx.scene.text.Text tableNumberText;
    
    // Pax (party size) display
    @FXML private javafx.scene.text.Text paxText;
    
    private int currentTableId;
    private int currentPax = 0; // Default to 0, will be set when order is created or table is selected
    private static int pendingPax = 0; // Temporary storage for pax when taking a new table
    
    public static void setPendingPax(int pax) {
        pendingPax = pax;
    }

    // ObservableLists for tables
    private final ObservableList<OrderDisplay> ordersList = FXCollections.observableArrayList();
    private final ObservableList<ReservationDisplay> reservationsList = FXCollections.observableArrayList();
    
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private Order currentOrder;

    @FXML
    private void initialize() {
        setupTables();
        loadReservations();
        // Removed loadTableButtons() - table buttons should only be in transactions.fxml

        // Button actions
        ordersButton.setOnAction(e -> {
            // Navigate to orders screen with current order or table
            Stage stage = (Stage) ordersButton.getScene().getWindow();
            if (currentOrder != null) {
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            } else if (currentTableId > 0) {
                // Pass table info so order can be created when first item is added
                Table table = new Table();
                table.setTableId(currentTableId);
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", table);
            } else {
                SceneNavigator.showError("No table or order selected.");
            }
        });
        reservationsButton.setOnAction(e -> {
            // Navigate to reservations screen
            SceneNavigator.switchScene(reservationsButton, "/Resources/Transactions/reservations.fxml");
        });
        backButton.setOnAction(e -> {
            // Navigate back to dashboard and load transactions in the dashboard content area
            Stage stage = (Stage) backButton.getScene().getWindow();
            SceneNavigator.switchToDashboard(stage);
        });

        // Example: dynamic language change
        setLanguage("EN"); // or "ES", "FR", etc.
        
        // If there's already an order set, load it
        if (currentOrder != null) {
            loadCurrentTableOrder();
        }
    }
    
    private void loadTableButtons() {
        if (tableButtonsPane == null) {
            // If FlowPane is not in FXML, we'll create one dynamically
            // For now, just log warning
            System.out.println("WARNING: tableButtonsPane not found in FXML");
            return;
        }
        
        // Clear existing buttons
        tableButtonsPane.getChildren().clear();
        
        // Get all tables
        List<Table> allTables = TableDAO.getAllTables();
        if (allTables == null || allTables.isEmpty()) {
            System.out.println("No tables found");
            return;
        }
        
        // Create a button for each table
        for (Table table : allTables) {
            Button tableButton = new Button("Table " + table.getTableId());
            tableButton.setPrefWidth(90);
            tableButton.setPrefHeight(50);
            tableButton.setPadding(new Insets(5));
            tableButton.setFont(javafx.scene.text.Font.font(12));
            tableButton.setStyle("-fx-background-radius: 50; -fx-border-radius: 50;");
            
            // Check if table has an active order (status = OPEN)
            Order activeOrder = getActiveOrderForTable(table.getTableId());
            boolean hasActiveOrder = (activeOrder != null && activeOrder.getStatus() == OrderStatus.OPEN);
            
            if (hasActiveOrder) {
                tableButton.setStyle("-fx-background-color: #818589; -fx-text-fill: white; -fx-font-weight: bold;"); 
            } else {
                tableButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;"); // Green
            }
            
            // Set button action: when clicked, load the active order for this table
            final int tableId = table.getTableId(); // Make effectively final
            tableButton.setOnAction(e -> {
                Order order = getActiveOrderForTable(tableId);
                if (order != null) {
                    currentOrder = order;
                    loadCurrentTableOrder();
                    // Navigate to orders screen
                    Stage stage = (Stage) tableButton.getScene().getWindow();
                    SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", order);
                } else {
                    // No active order, show message
                    SceneNavigator.showInfo("Table " + tableId + " has no active order.");
                }
            });
            
            tableButtonsPane.getChildren().add(tableButton);
        }
    }
    
    /**
     * Get the active (OPEN) order for a table
     */
    private Order getActiveOrderForTable(int tableId) {
        Order order = OrderDB.getWholeOrderByTable(tableId);
        // Check if order exists and is OPEN (active)
        if (order != null && order.getStatus() == OrderStatus.OPEN) {
            // Also check if it has active order items
            if (order.getOrderItems() != null) {
                boolean hasActiveItems = order.getOrderItems().stream()
                    .anyMatch(item -> item.getStatus() != null && item.getStatus());
                if (hasActiveItems) {
                    return order;
                }
            }
        }
        return null;
    }

    private void setupTables() {
        menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ordersTable.setItems(ordersList);

        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reservationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        reservationsTable.setItems(reservationsList);
    }

    /**
     * Called by SceneNavigator when navigating from table selection
     */
    public void setData(Object data) {
        System.out.println("TransactionMenuUI.setData() called with: " + (data != null ? data.getClass().getName() : "null"));
        
        if (data instanceof Order) {
            currentOrder = (Order) data;
            currentTableId = currentOrder.getTableId();
            System.out.println("Order received - Order ID: " + currentOrder.getOrderId() + ", Table ID: " + currentOrder.getTableId());
            updateTableNumberDisplay(currentOrder.getTableId());
            updatePaxDisplay(); // Update pax display
            loadCurrentTableOrder();
            loadReservations(); // Load reservations when order is set
            
            // Check if order is empty (all orders cancelled)
            checkAndOfferTableAvailability();
        } else if (data instanceof Table) {
            Table table = (Table) data;
            currentTableId = table.getTableId();
            System.out.println("Table received - Table ID: " + table.getTableId());
            updateTableNumberDisplay(table.getTableId());
            
            // Check if there's a pending pax value (set when taking a new table)
            if (pendingPax > 0) {
                currentPax = pendingPax;
                pendingPax = 0; // Reset after using
                updatePaxDisplay();
            }
            
            // Fetch order for this table
            Order order = OrderDB.getWholeOrderByTable(table.getTableId());
            if (order != null) {
                currentOrder = order;
                updatePaxDisplay(); // Update pax display (will calculate from items)
                loadCurrentTableOrder();
                loadReservations(); // Load reservations when order is set
            } else {
                System.out.println("No order found for table " + table.getTableId());
                currentOrder = null;
                // Keep currentPax if it was set from pendingPax, otherwise set to 0
                if (currentPax == 0) {
                    updatePaxDisplay();
                }
                ordersList.clear();
            }
        }
    }
    
    private void updateTableNumberDisplay(int tableId) {
        if (tableNumberText != null) {
            tableNumberText.setText("Table: " + tableId);
        }
    }
    
    private void updatePaxDisplay() {
        if (paxText != null) {
            if (currentOrder != null && currentOrder.getOrderItems() != null && !currentOrder.getOrderItems().isEmpty()) {
                // Calculate total quantity of all items as pax
                int totalPax = currentOrder.getOrderItems().stream()
                    .mapToInt(item -> item.getQuantity())
                    .sum();
                currentPax = totalPax;
                paxText.setText("Pax: " + totalPax);
            } else if (currentPax > 0) {
                // Use manually entered pax if no order items yet
                paxText.setText("Pax: " + currentPax);
            } else {
                paxText.setText("Pax: 0");
            }
        }
    }
    
    private void checkAndOfferTableAvailability() {
        // Check if order exists but has no items, and table is marked as taken
        if (currentOrder != null && currentTableId > 0) {
            boolean hasNoItems = (currentOrder.getOrderItems() == null || currentOrder.getOrderItems().isEmpty());
            
            if (hasNoItems) {
                // Check if table is marked as taken
                TableDAO tableDAO = new TableDAO();
                Table table = tableDAO.getTableById(currentTableId);
                if (table != null && !table.getTableStatus()) {
                    // Table is marked as taken but has no orders - offer to mark as available
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("All Orders Cancelled");
                    alert.setHeaderText("All orders cancelled for Table " + currentTableId);
                    alert.setContentText("Table is now available. Would you like to mark it as available?");
                    
                    ButtonType yesButton = new ButtonType("Mark as Available");
                    ButtonType noButton = new ButtonType("Cancel");
                    alert.getButtonTypes().setAll(yesButton, noButton);
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == yesButton) {
                        // Mark table as available
                        table.setTableStatus(true);
                        if (tableDAO.updateTable(table)) {
                            SceneNavigator.showInfo("Table " + currentTableId + " is now available.");
                        } else {
                            SceneNavigator.showError("Failed to update table status.");
                        }
                    }
                }
            }
        }
    }
    
    private void loadCurrentTableOrder() {
        ordersList.clear();
        
        if (currentOrder == null || currentOrder.getOrderItems() == null || currentOrder.getOrderItems().isEmpty()) {
            System.out.println("No order items to display");
            updatePaxDisplay(); // Update pax to 0
            return;
        }
        
        System.out.println("Loading order items for Order ID: " + currentOrder.getOrderId());
        
        for (OrderItem item : currentOrder.getOrderItems()) {
            // Get menu item name instead of just ID
            MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
            String itemName;
            if (menuItem != null) {
                itemName = menuItem.getMenuName();
            } else {
                itemName = "Menu ID: " + item.getMenuId(); // Fallback if menu item not found
            }
            
            ordersList.add(new OrderDisplay(itemName, String.valueOf(item.getQuantity())));
            System.out.println("Added: " + itemName + " x" + item.getQuantity());
        }
        
        System.out.println("Loaded " + ordersList.size() + " order items");
        updatePaxDisplay(); // Update pax after loading items
    }

    private void loadOrders() {
        ordersList.clear();
        List<Order> allOrders = OrderDB.getOrdersByStaffId(1); // TODO: Replace with currentStaffId

        for (Order o : allOrders) {
            if (o.getOrderItems() != null) {
                for (OrderItem item : o.getOrderItems()) {
                    // Get menu item name instead of just ID
                    MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
                    String itemName;
                    if (menuItem != null) {
                        itemName = menuItem.getMenuName();
                    } else {
                        itemName = "Menu ID: " + item.getMenuId();
                    }
                    ordersList.add(new OrderDisplay(itemName, String.valueOf(item.getQuantity())));
                }
            }
        }
    }

    private void loadReservations() {
        reservationsList.clear();
        // Load real reservations from DAO
        if (currentOrder != null) {
            int tableId = currentOrder.getTableId();
            ArrayList<Reservations> allReservations = Controller.ReservationController.getAllReservations();
            for (Reservations res : allReservations) {
                if (res.getTableId() == tableId && res.getIsActive()) {
                    String dateStr = res.getDateAndTime().toLocalDate().toString();
                    String name = res.getReserveName();
                    reservationsList.add(new ReservationDisplay(dateStr, name));
                }
            }
        }
    }


    /** Change UI language dynamically (buttons for now) */
    private void setLanguage(String lang) {
        switch (lang) {
            case "ES": // Spanish
                ordersButton.setText("PEDIDOS");
                reservationsButton.setText("RESERVAS");
                backButton.setText("ATRÁS");
                break;
            case "FR": // French
                ordersButton.setText("COMMANDES");
                reservationsButton.setText("RÉSERVATIONS");
                backButton.setText("RETOUR");
                break;
            default: // English
                ordersButton.setText("Orders");
                reservationsButton.setText("Reservations");
                backButton.setText("← BACK");
        }
    }

    // TableView models
    public static class OrderDisplay {
        private final String itemName;
        private final String quantity;

        public OrderDisplay(String itemName, String quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }

        public String getItemName() { return itemName; }
        public String getQuantity() { return quantity; }
    }

    public static class ReservationDisplay {
        private final String date;
        private final String name;

        public ReservationDisplay(String date, String name) {
            this.date = date;
            this.name = name;
        }

        public String getDate() { return date; }
        public String getName() { return name; }
    }
}
