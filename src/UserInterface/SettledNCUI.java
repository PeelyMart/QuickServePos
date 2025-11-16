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

import java.math.BigDecimal;

public class SettledNCUI {

    @FXML
    private Button cancelButton, paidButton, backButton;

    @FXML
    private TextArea notesArea;

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

        // Display payment method and total in notes area
        if (notesArea != null) {
            String paymentMethod = currentOrder.getPaymentMethod() != null 
                ? currentOrder.getPaymentMethod().name() 
                : "CARD";
            String display = String.format(
                "Payment Method: %s\n" +
                "Total Amount: $%.2f\n" +
                "Transaction will be processed immediately.",
                paymentMethod, totalDue
            );
            notesArea.setText(display);
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

        // Get current staff
        Staff currentStaff = UserService.getCurrentUser();
        if (currentStaff == null) {
            SceneNavigator.showError("Staff information not found. Please login again.");
            return;
        }

        // Update order total cost
        currentOrder.setTotalCost(totalDue);
        currentOrder.setStaffId(currentStaff.getStaffId());

        // Record payment (as non-member for now - member info would be passed separately if needed)
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

        // Show success message
        String paymentMethod = currentOrder.getPaymentMethod() != null 
            ? currentOrder.getPaymentMethod().name() 
            : "CARD";
        String message = String.format(
            "Payment Successful!\n\n" +
            "Payment Method: %s\n" +
            "Amount: $%.2f\n\n" +
            "Order has been closed.",
            paymentMethod, totalDue
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