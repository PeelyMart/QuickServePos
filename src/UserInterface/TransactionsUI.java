package UserInterface;

import Controller.TableActions;
import DAO.OrderDB;
import DAO.TableDAO;
import Model.Order;
import Model.OrderStatus;
import Model.Table;
import Model.Staff;
import Controller.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class TransactionsUI {

    @FXML
    private FlowPane tableContainer;
    
    // Use TilePane for better column control
    private TilePane tilePane;
    
    private static final int COLUMNS = 5;

    private int currentStaffId;

    @FXML
    public void initialize() {
        Staff currentUser = UserService.getCurrentUser();
        if (currentUser != null) {
            currentStaffId = currentUser.getStaffId();
        } else {
            System.err.println("No user logged in!");
            return;
        }

        // Load tables when initialized - this will be called fresh each time the screen loads
        loadTablesFromDB();
    }
    
    /**
     * Public method to refresh table buttons - can be called when returning to this screen
     */
    public void refreshTables() {
        loadTablesFromDB();
    }

    private void loadTablesFromDB() {
        ArrayList<Table> tables = new ArrayList<>(TableDAO.getAllTables());
        
        // Sort tables by ID to ensure proper ordering (1, 2, 3, 4, 5, 6, 7, etc.)
        Collections.sort(tables, Comparator.comparingInt(Table::getTableId));
        
        tableContainer.getChildren().clear();
        
        // Create TilePane with exactly 5 columns for better control
        if (tilePane == null) {
            tilePane = new TilePane();
            tilePane.setPrefColumns(COLUMNS);
            tilePane.setHgap(20);
            tilePane.setVgap(20);
            tableContainer.getChildren().add(tilePane);
        } else {
            tilePane.getChildren().clear();
        }
        
        // Debug: Print table info and create buttons in one pass
        System.out.println("=== Loading Tables ===");
        for (Table t : tables) {
            // Query order once per table
            Order activeOrder = OrderDB.getWholeOrderByTable(t.getTableId());
            boolean hasOpenOrder = (activeOrder != null);
            boolean isTableAvailable = t.getTableStatus(); // true = available, false = taken
            
            // Debug output
            System.out.println("Table " + t.getTableId() + " - Status: " + isTableAvailable + ", Has Open Order: " + hasOpenOrder);
            if (activeOrder != null) {
                System.out.println("  Order ID: " + activeOrder.getOrderId() + ", Status: " + activeOrder.getStatus());
            } else {
                System.out.println("  No open order found");
            }
            
            // Create button
            Button tableButton = new Button(String.valueOf(t.getTableId()));
            tableButton.setPrefSize(90, 90);
            tableButton.setMinSize(90, 90);
            tableButton.setMaxSize(90, 90);
            tableButton.setFont(javafx.scene.text.Font.font(14));
            tableButton.setWrapText(false);

            // Set button color based on table availability and order status
            // Grey = table is taken (unavailable) OR has OPEN order
            // Green = table is available AND no OPEN order
            if (!isTableAvailable || hasOpenOrder) {
                // Grey = table is taken or has OPEN order (unavailable)
                tableButton.setStyle("-fx-background-color: #808080; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                // Green = table is available and no OPEN order
                tableButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-font-weight: bold; -fx-font-size: 14px;");
            }

            tableButton.setOnAction(e -> handleTableClick(t));
            tilePane.getChildren().add(tableButton);
        }
    }

    private void handleTableClick(Table table) {
        Stage stage = (Stage) tableContainer.getScene().getWindow(); // get current stage

        // Always refresh the table data from database before checking
        TableDAO tableDAO = new TableDAO();
        Table freshTable = tableDAO.getTableById(table.getTableId());
        if (freshTable == null) {
            SceneNavigator.showError("Table not found in database.");
            return;
        }

        // Check if table has an active order first
        Order existingOrder = OrderDB.getWholeOrderByTable(freshTable.getTableId());
        if (existingOrder != null) {
            // Table has active order → navigate directly to it (no confirmation needed)
            SceneNavigator.switchNoButton(stage, "/Resources/Transactions/transactionMenu.fxml", existingOrder);
            return;
        }

        // Table has no active order - check if it's available
        if (!freshTable.getTableStatus()) { 
            // Table is marked as taken but has no order - offer to mark it as available
            Alert markAvailableDialog = new Alert(Alert.AlertType.CONFIRMATION);
            markAvailableDialog.setTitle("Table Status");
            markAvailableDialog.setHeaderText("Table " + freshTable.getTableId() + " is marked as taken but has no active order.");
            markAvailableDialog.setContentText("Would you like to mark this table as available?");
            
            ButtonType yesButton = new ButtonType("Mark as Available");
            ButtonType noButton = new ButtonType("Cancel");
            markAvailableDialog.getButtonTypes().setAll(yesButton, noButton);
            
            Optional<ButtonType> result = markAvailableDialog.showAndWait();
            if (result.isPresent() && result.get() == yesButton) {
                // Mark table as available
                freshTable.setTableStatus(true);
                if (tableDAO.updateTable(freshTable)) {
                    SceneNavigator.showInfo("Table " + freshTable.getTableId() + " is now available.");
                    loadTablesFromDB(); // Refresh buttons to show updated status
                } else {
                    SceneNavigator.showError("Failed to update table status.");
                }
            }
            return;
        }

        // table available and no active order → ask for confirmation and pax
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Take Table");
        confirmDialog.setHeaderText("Take Table " + freshTable.getTableId() + "?");
        confirmDialog.setContentText("Do you want to take this table?");
        
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        confirmDialog.getButtonTypes().setAll(yesButton, noButton);
        
        Optional<ButtonType> confirmResult = confirmDialog.showAndWait();
        if (confirmResult.isPresent() && confirmResult.get() == yesButton) {
            // Ask for pax
            TextInputDialog paxDialog = new TextInputDialog("1");
            paxDialog.setTitle("Party Size");
            paxDialog.setHeaderText("How many pax?");
            paxDialog.setContentText("Enter number of people:");
            
            Optional<String> paxResult = paxDialog.showAndWait();
            if (paxResult.isPresent() && !paxResult.get().trim().isEmpty()) {
                try {
                    int pax = Integer.parseInt(paxResult.get().trim());
                    if (pax <= 0) {
                        SceneNavigator.showError("Pax must be greater than 0.");
                        return;
                    }
                    
                    // Mark table as taken
                    freshTable.setTableStatus(false);
                    tableDAO.updateTable(freshTable);
                    
                    // Store pax temporarily in TransactionMenuUI static variable
                    TransactionMenuUI.setPendingPax(pax);
                    
                    // Pass table to TransactionMenuUI (pax will be picked up from static variable)
                    SceneNavigator.switchNoButton(stage, "/Resources/Transactions/transactionMenu.fxml", freshTable);
                    
                    // Refresh buttons to show updated color
                    loadTablesFromDB();
                    
                } catch (NumberFormatException e) {
                    SceneNavigator.showError("Please enter a valid number for pax.");
                    return;
                }
            } else {
                // User cancelled pax input
                return;
            }
        } else {
            // User cancelled taking the table
            return;
        }
    }

}
