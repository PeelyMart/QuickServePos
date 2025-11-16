package UserInterface;

import DAO.OrderDB;
import Model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TransactionMenuUI {

    // Buttons
    @FXML private Button ordersButton, reservationsButton, takenButton, availableButton, backButton;

    // Orders Table
    @FXML private TableView<OrderDisplay> ordersTable;
    @FXML private TableColumn<OrderDisplay, String> menuItemColumn;
    @FXML private TableColumn<OrderDisplay, String> quantityColumn;

    // Reservations Table
    @FXML private TableView<ReservationDisplay> reservationsTable;
    @FXML private TableColumn<ReservationDisplay, String> reservationDateColumn;
    @FXML private TableColumn<ReservationDisplay, String> reservationQtyColumn;

    // ObservableLists for tables
    private final ObservableList<OrderDisplay> ordersList = FXCollections.observableArrayList();
    private final ObservableList<ReservationDisplay> reservationsList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTables();
        loadOrders();
        loadReservations();

        // Button actions
        ordersButton.setOnAction(e -> loadOrders());
        reservationsButton.setOnAction(e -> loadReservations());
        takenButton.setOnAction(e -> filterTables(true));
        availableButton.setOnAction(e -> filterTables(false));
        backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml"));

        // Example: dynamic language change
        setLanguage("EN"); // or "ES", "FR", etc.
    }

    private void setupTables() {
        menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        ordersTable.setItems(ordersList);

        reservationDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        reservationQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        reservationsTable.setItems(reservationsList);
    }

    private void loadOrders() {
        ordersList.clear();
        List<Order> allOrders = OrderDB.getOrdersByStaffId(1); // TODO: Replace with currentStaffId

        for (Order o : allOrders) {
            if (o.getOrderItems() != null) {
                o.getOrderItems().forEach(item ->
                        ordersList.add(new OrderDisplay(item.getMenuId() + "", item.getQuantity() + ""))
                );
            }
        }
    }

    private void loadReservations() {
        reservationsList.clear();
        // TODO: Replace with real DAO
        reservationsList.add(new ReservationDisplay("2025-11-16", "3"));
        reservationsList.add(new ReservationDisplay("2025-11-17", "2"));
    }

    private void filterTables(boolean showTaken) {
        SceneNavigator.testClick(showTaken ? "Showing TAKEN tables" : "Showing AVAILABLE tables");
    }

    /** Change UI language dynamically (buttons for now) */
    private void setLanguage(String lang) {
        switch (lang) {
            case "ES": // Spanish
                ordersButton.setText("PEDIDOS");
                reservationsButton.setText("RESERVAS");
                backButton.setText("ATRÁS");
                takenButton.setText("OCUPADAS");
                availableButton.setText("DISPONIBLES");
                break;
            case "FR": // French
                ordersButton.setText("COMMANDES");
                reservationsButton.setText("RÉSERVATIONS");
                backButton.setText("RETOUR");
                takenButton.setText("PRISES");
                availableButton.setText("DISPONIBLES");
                break;
            default: // English
                ordersButton.setText("Orders");
                reservationsButton.setText("Reservations");
                backButton.setText("← BACK");
                takenButton.setText("TAKEN");
                availableButton.setText("AVAILABLE");
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
        private final String quantity;

        public ReservationDisplay(String date, String quantity) {
            this.date = date;
            this.quantity = quantity;
        }

        public String getDate() { return date; }
        public String getQuantity() { return quantity; }
    }
}
