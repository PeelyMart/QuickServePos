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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.ArrayList;
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

    // ObservableLists for tables
    private final ObservableList<OrderDisplay> ordersList = FXCollections.observableArrayList();
    private final ObservableList<ReservationDisplay> reservationsList = FXCollections.observableArrayList();
    
    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private Order currentOrder;

    @FXML
    private void initialize() {
        setupTables();
        loadReservations();
        loadTableButtons(); // Load dynamic table buttons

        // Button actions
        ordersButton.setOnAction(e -> {
            // Navigate to orders screen with current order (if available)
            if (currentOrder != null) {
                Stage stage = (Stage) ordersButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            } else {
                // Navigate to orders screen without order data
                SceneNavigator.switchScene(ordersButton, "/Resources/Transactions/orders.fxml");
            }
        });
        reservationsButton.setOnAction(e -> {
            // Navigate to reservations screen
            SceneNavigator.switchScene(reservationsButton, "/Resources/Transactions/reservations.fxml");
        });
        backButton.setOnAction(e -> {
            // Navigate back to dashboard - transactions will show in dashboard content
            SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml");
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
            Button tableButton = new Button(String.valueOf(table.getTableId()));
            tableButton.setPrefWidth(50);
            tableButton.setPrefHeight(50);
            tableButton.setPadding(new Insets(5));
            tableButton.setFont(javafx.scene.text.Font.font(14));
            tableButton.setStyle("-fx-background-radius: 50; -fx-border-radius: 50;");
            
            // Check if table has an active order (status = OPEN)
            Order activeOrder = getActiveOrderForTable(table.getTableId());
            boolean hasActiveOrder = (activeOrder != null && activeOrder.getStatus() == OrderStatus.OPEN);
            
            // Set button color: Green = available (no active order), Red = occupied (has active order)
            if (hasActiveOrder) {
                tableButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;"); // Red
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
            System.out.println("Order received - Order ID: " + currentOrder.getOrderId() + ", Table ID: " + currentOrder.getTableId());
            loadCurrentTableOrder();
            loadReservations(); // Load reservations when order is set
        } else if (data instanceof Table) {
            Table table = (Table) data;
            System.out.println("Table received - Table ID: " + table.getTableId());
            // Fetch order for this table
            Order order = OrderDB.getWholeOrderByTable(table.getTableId());
            if (order != null) {
                currentOrder = order;
                loadCurrentTableOrder();
                loadReservations(); // Load reservations when order is set
            } else {
                System.out.println("No order found for table " + table.getTableId());
                ordersList.clear();
            }
        }
    }
    
    private void loadCurrentTableOrder() {
        ordersList.clear();
        
        if (currentOrder == null || currentOrder.getOrderItems() == null || currentOrder.getOrderItems().isEmpty()) {
            System.out.println("No order items to display");
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
