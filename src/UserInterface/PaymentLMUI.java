package UserInterface;

import DAO.LoyaltymemberDAO;
import DAO.OrderDB;
import Model.LoyaltyMember;
import Model.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PaymentLMUI {

    @FXML
    private Button searchButton;

    @FXML
    private Button backButton;

    @FXML
    private Button payButton;

    @FXML
    private TextField memberIdField;

    @FXML
    private Label memberStatusLabel;

    @FXML
    private TextArea memberInfoArea;

    @FXML
    private TextArea orderSummaryArea;

    @FXML
    private TextArea totalArea;

    private Order currentOrder;
    private LoyaltyMember currentMember;
    private BigDecimal originalTotal;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private static final BigDecimal MEMBER_DISCOUNT_PERCENT = new BigDecimal("0.10"); // 10% discount

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

        // Calculate original total
        originalTotal = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        // If member is already set, apply discount
        if (currentMember != null) {
            applyDiscount();
        } else {
            updateTotalDisplay();
        }
    }

    @FXML
    private void handleSearch() {
        String memberIdStr = memberIdField.getText().trim();
        if (memberIdStr.isEmpty()) {
            memberStatusLabel.setText("Please enter a Member ID");
            memberStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            return;
        }

        try {
            int memberId = Integer.parseInt(memberIdStr);
            LoyaltymemberDAO dao = new LoyaltymemberDAO();
            LoyaltyMember member = dao.getLoyaltyMemberById(memberId);

            if (member == null) {
                memberStatusLabel.setText("INVALID - Member not found");
                memberStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
                memberInfoArea.setText("");
                currentMember = null;
                updateTotalDisplay();
            } else {
                if (!"active".equalsIgnoreCase(member.getStatus())) {
                    memberStatusLabel.setText("INVALID - Member account is not active");
                    memberStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
                    memberInfoArea.setText("");
                    currentMember = null;
                    updateTotalDisplay();
                } else {
                    memberStatusLabel.setText("VALID");
                    memberStatusLabel.setStyle("-fx-text-fill: #51cf66;");
                    memberInfoArea.setText(String.format(
                        "Member ID: %d\nName: %s\nPoints: %d\nStatus: %s",
                        member.getCustomerId(), member.getName(), member.getPoints(), member.getStatus()
                    ));
                    currentMember = member;
                    applyDiscount();
                }
            }
        } catch (NumberFormatException e) {
            memberStatusLabel.setText("INVALID - Please enter a valid number");
            memberStatusLabel.setStyle("-fx-text-fill: #ff6b6b;");
            memberInfoArea.setText("");
            currentMember = null;
            updateTotalDisplay();
        }
    }

    private void applyDiscount() {
        if (currentOrder == null) {
            return;
        }

        originalTotal = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        if (currentMember != null && "active".equalsIgnoreCase(currentMember.getStatus())) {
            // Apply 10% discount
            discountAmount = originalTotal.multiply(MEMBER_DISCOUNT_PERCENT)
                    .setScale(2, RoundingMode.HALF_UP);
            finalTotal = originalTotal.subtract(discountAmount);
        } else {
            discountAmount = BigDecimal.ZERO;
            finalTotal = originalTotal;
        }
        
        updateTotalDisplay();
    }

    private void updateTotalDisplay() {
        if (totalArea == null || originalTotal == null) {
            return;
        }

        if (currentMember != null && "active".equalsIgnoreCase(currentMember.getStatus())) {
            String display = String.format(
                "Original: $%.2f\nDiscount (10%%): -$%.2f\nFinal Total: $%.2f",
                originalTotal, discountAmount, finalTotal
            );
            totalArea.setText(display);
        } else {
            totalArea.setText(String.format("$%.2f", originalTotal));
            finalTotal = originalTotal;
        }
    }

    @FXML
    private void initialize() {
        // PAY button → go to payment method
        if (payButton != null) {
            payButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                if (currentMember == null) {
                    SceneNavigator.showWarning("No member selected. Proceeding as non-member.");
                }
                // Store member info in order or pass separately
                javafx.stage.Stage stage = (javafx.stage.Stage) payButton.getScene().getWindow();
                // Pass both order and member data (we'll create a wrapper or pass order with member ID stored)
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

        // SEARCH button
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }

        // Allow Enter key in memberIdField to trigger search
        if (memberIdField != null) {
            memberIdField.setOnAction(e -> handleSearch());
        }
    }
}