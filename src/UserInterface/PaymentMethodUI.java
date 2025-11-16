package UserInterface;

import DAO.OrderDB;
import Model.Order;
import Model.Payment;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.math.BigDecimal;

public class PaymentMethodUI {

    @FXML
    private TextArea balanceArea;

    @FXML
    private Button cardButton, cashButton, onlineButton, backButton;

    private Order currentOrder;
    private BigDecimal finalTotal;

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

        // Get final total (could be discounted if member)
        // For now, use order total - in real implementation, pass discounted total from previous screen
        finalTotal = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        if (balanceArea != null) {
            balanceArea.setText(String.format("₱%.2f", finalTotal));
        }
    }

    @FXML
    private void initialize() {
        // CARD button → card payment (use DEBIT as card payment)
        if (cardButton != null) {
            cardButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                // Set payment method to DEBIT (card)
                currentOrder.setPaymentMethod(Payment.PaymentMethod.DEBIT);
                javafx.stage.Stage stage = (javafx.stage.Stage) cardButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledNC.fxml", currentOrder);
            });
        }

        // QR/ONLINE button → online payment (use CREDIT as online/QR payment)
        if (onlineButton != null) {
            onlineButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                // Set payment method to CREDIT (online/QR)
                currentOrder.setPaymentMethod(Payment.PaymentMethod.CREDIT);
                javafx.stage.Stage stage = (javafx.stage.Stage) onlineButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledNC.fxml", currentOrder);
            });
        }

        // CASH button → cash payment
        if (cashButton != null) {
            cashButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                // Set payment method to CASH
                currentOrder.setPaymentMethod(Payment.PaymentMethod.CASH);
                javafx.stage.Stage stage = (javafx.stage.Stage) cashButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledCash.fxml", currentOrder);
            });
        }

        // BACK button → return to previous payment screen
        if (backButton != null) {
            backButton.setOnAction(e -> {
                // Go back to payment choice (member or non-member)
                // For simplicity, go back to orders screen
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            });
        }
    }
}