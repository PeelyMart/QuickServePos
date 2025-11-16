package UserInterface;

import Controller.PaymentControl;
import Controller.UserService;
import DAO.OrderDB;
import Model.Order;
import Model.OrderStatus;
import Model.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class SettledCashUI {

    @FXML
    private Button cancelButton, paidButton, backButton;

    @FXML
    private TextField amountPaidField;

    @FXML
    private TextArea changeDueArea;

    @FXML
    private TextArea totalDueArea;

    private Order currentOrder;
    private BigDecimal totalDue;

    @FXML
    public void setData(Object data) {
        if (data instanceof Order) {
            currentOrder = (Order) data;
            loadOrderData();
        }
    }

    private void loadOrderData() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order found.");
            return;
        }

        // Reload order to get latest data
        Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
        if (refreshedOrder != null) {
            currentOrder = refreshedOrder;
        }

        // Get total due
        totalDue = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        if (totalDueArea != null) {
            totalDueArea.setText(String.format("$%.2f", totalDue));
        }

        // Set up listener for amount paid field to calculate change
        if (amountPaidField != null) {
            amountPaidField.textProperty().addListener((observable, oldValue, newValue) -> {
                calculateChange();
            });
        }
    }

    private void calculateChange() {
        if (amountPaidField == null || changeDueArea == null || totalDue == null) {
            return;
        }

        String amountPaidStr = amountPaidField.getText().trim();
        if (amountPaidStr.isEmpty()) {
            changeDueArea.setText("$0.00");
            return;
        }

        try {
            BigDecimal amountPaid = new BigDecimal(amountPaidStr);
            BigDecimal change = amountPaid.subtract(totalDue);

            if (change.compareTo(BigDecimal.ZERO) < 0) {
                changeDueArea.setText(String.format("$%.2f (Insufficient)", change.abs()));
                changeDueArea.setStyle("-fx-text-fill: #ff6b6b;");
            } else {
                changeDueArea.setText(String.format("$%.2f", change));
                changeDueArea.setStyle("-fx-text-fill: #51cf66;");
            }
        } catch (NumberFormatException e) {
            changeDueArea.setText("Invalid amount");
            changeDueArea.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void initialize() {
        // PAID button → complete payment
        if (paidButton != null) {
            paidButton.setOnAction(e -> handlePayment());
        }

        // CANCEL button → cancel payment
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancel Payment");
                alert.setHeaderText("Are you sure you want to cancel?");
                alert.setContentText("This will return you to the order screen without processing payment.");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                ButtonType result = alert.showAndWait().orElse(noButton);
                if (result == yesButton) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) cancelButton.getScene().getWindow();
                    SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
                }
            });
        }

        // BACK button → return to payment method selection
        if (backButton != null) {
            backButton.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
            });
        }
    }

    private void handlePayment() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order data available.");
            return;
        }

        // Validate amount paid
        String amountPaidStr = amountPaidField.getText().trim();
        if (amountPaidStr.isEmpty()) {
            SceneNavigator.showError("Please enter the amount paid.");
            return;
        }

        BigDecimal amountPaid;
        try {
            amountPaid = new BigDecimal(amountPaidStr);
        } catch (NumberFormatException e) {
            SceneNavigator.showError("Please enter a valid amount.");
            return;
        }

        if (amountPaid.compareTo(totalDue) < 0) {
            SceneNavigator.showError("Amount paid is less than total due. Cannot complete payment.");
            return;
        }

        // Get current staff
        Staff currentStaff = UserService.getCurrentUser();
        if (currentStaff == null) {
            SceneNavigator.showError("Staff information not found. Please login again.");
            return;
        }

        // Update order total cost (in case there was discount)
        currentOrder.setTotalCost(totalDue);
        currentOrder.setStaffId(currentStaff.getStaffId());

        // Record payment (as non-member for cash - member info would be passed separately if needed)
        int paymentResult = PaymentControl.initiatePayment(currentOrder, 0); // 0 = non-member

        if (paymentResult == -1) {
            SceneNavigator.showError("Cannot process payment: Order total is zero.");
            return;
        } else if (paymentResult == 0) {
            SceneNavigator.showError("Payment processing failed. Please try again.");
            return;
        }

        // Close order (update status to CLOSED)
        closeOrder();

        // Show success message with change
        BigDecimal change = amountPaid.subtract(totalDue);
        String message = String.format(
            "Payment Successful!\n\n" +
            "Total: $%.2f\n" +
            "Amount Paid: $%.2f\n" +
            "Change: $%.2f\n\n" +
            "Order has been closed.",
            totalDue, amountPaid, change
        );

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Payment Complete");
        successAlert.setHeaderText("Payment Successful");
        successAlert.setContentText(message);
        successAlert.showAndWait();

        // Return to transaction menu
        javafx.stage.Stage stage = (javafx.stage.Stage) paidButton.getScene().getWindow();
        SceneNavigator.switchScene(paidButton, "/Resources/Transactions/transactionMenu.fxml");
    }

    private void closeOrder() {
        // Update order status to CLOSED
        // We need to add this method to OrderDB, but for now, we'll update it directly
        String sql = "UPDATE order_header SET status = ? WHERE order_id = ?";
        try (java.sql.Connection conn = DAO.DB.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, OrderStatus.CLOSED.toSqlString());
            stmt.setInt(2, currentOrder.getOrderId());
            stmt.executeUpdate();
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error closing order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}