package UserInterface;

import DAO.OrderDB;
import Model.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.math.BigDecimal;

public class PaymentNormalUI {

    @FXML
    private Button pwdButton;

    @FXML
    private Button backButton;

    @FXML
    private Button payButton;

    @FXML
    private TextArea orderSummaryArea;

    @FXML
    private TextArea totalArea;

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

        // Build order summary
        StringBuilder summary = new StringBuilder();
        summary.append("Order ID: ").append(currentOrder.getOrderId()).append("\n");
        summary.append("Table ID: ").append(currentOrder.getTableId()).append("\n");
        summary.append("Order Time: ").append(currentOrder.getOrderTime()).append("\n\n");
        summary.append("Items:\n");
        
        if (currentOrder.getOrderItems() != null) {
            for (var item : currentOrder.getOrderItems()) {
                summary.append(String.format("  - Item: %d, Qty: %d, Subtotal: $%.2f\n", 
                    item.getMenuId(), item.getQuantity(), item.getSubtotal()));
            }
        }
        
        if (orderSummaryArea != null) {
            orderSummaryArea.setText(summary.toString());
        }

        // Calculate total (no discount for non-members)
        finalTotal = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        if (totalArea != null) {
            totalArea.setText(String.format("$%.2f", finalTotal));
        }
    }

    @FXML
    private void initialize() {
        // PAY button → go to payment method
        if (payButton != null) {
            payButton.setOnAction(e -> {
                if (currentOrder == null || finalTotal == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                // Pass order data to payment method screen
                javafx.stage.Stage stage = (javafx.stage.Stage) payButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
            });
        }

        // BACK button → go back to orders
        if (backButton != null) {
            backButton.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            });
        }

        // PWD button → placeholder (could implement PWD discount)
        if (pwdButton != null) {
            pwdButton.setOnAction(e -> {
                SceneNavigator.showInfo("PWD discount feature coming soon.");
            });
        }
    }
}