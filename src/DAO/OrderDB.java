package DAO;

import Controller.OrderController;
import Model.Order;
import Model.OrderItem;
import Model.OrderStatus;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDB {


    /*
        @return - id of newly generated record
        @return - -1 if failed row insert
     */
    public static int newOrder(int tableId, int staffId) {
        String sql = "INSERT INTO order_header (table_id, staff_id, total_cost, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tableId);
            stmt.setInt(2, staffId);
            stmt.setBigDecimal(3, BigDecimal.valueOf(0.00));                 // total cost (BigDecimal)
            stmt.setString(4, OrderStatus.OPEN.toSqlString());          // enum -> SQL string

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);              // get auto-generated order_id
                    System.out.println("Order opened with ID: " + orderId);
                    return orderId;
                }
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Fetches an entire order (header + items) by its ID.
     *
     * @param headerId The order_id to look up.
     * @return The full Order object (including its list of OrderItems),
     *         or null if no matching order header was found.
     *
     * ⚙️ NOTE:
     * - Returns null only if the order header itself does not exist.
     * - The order’s item list will be empty if no items are found (never null).
     */
    public static Order getWholeOrder(int headerId) {
        String headerQuery = "SELECT * FROM order_header WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement headerStmt = conn.prepareStatement(headerQuery)) {

            headerStmt.setInt(1, headerId);
            ResultSet rsHeader = headerStmt.executeQuery();

            if (rsHeader.next()) {
                int orderId = rsHeader.getInt("order_id");
                int tableId = rsHeader.getInt("table_id");
                int staffId = rsHeader.getInt("staff_id");
                Timestamp ts = rsHeader.getTimestamp("order_time");
                BigDecimal totalCost = rsHeader.getBigDecimal("total_cost");
                String statusStr = rsHeader.getString("status");

                // Build the Order header
                Order order = new Order(orderId, tableId, staffId,
                        ts.toLocalDateTime(), totalCost, OrderStatus.fromString(statusStr));

                // Fetch related order items (may return an empty list)
                List<OrderItem> itemArray = OrderitemDAO.getOrderItemsByOrderId(orderId);
                order.setOrderItems(itemArray);
                OrderController.updateTotal(order);

                return order;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static Order getWholeOrderByTable(int tableId) {
        String headerQuery = "SELECT * FROM order_header WHERE table_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement headerStmt = conn.prepareStatement(headerQuery)) {

            headerStmt.setInt(1, tableId );
            ResultSet rsHeader = headerStmt.executeQuery();

            if (rsHeader.next()) {
                int orderId = rsHeader.getInt("order_id");
                int table_id = rsHeader.getInt("table_id");
                int staffId = rsHeader.getInt("staff_id");
                Timestamp ts = rsHeader.getTimestamp("order_time");
                BigDecimal totalCost = rsHeader.getBigDecimal("total_cost");
                String statusStr = rsHeader.getString("status");

                // Build the Order header
                Order order = new Order(orderId, table_id, staffId,
                        ts.toLocalDateTime(), totalCost, OrderStatus.fromString(statusStr));

                // Fetch related order items (may return an empty list)
                List<OrderItem> itemArray = OrderitemDAO.getOrderItemsByOrderId(orderId);
                order.setOrderItems(itemArray);
                OrderController.updateTotal(order);

                return order;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Order> getOrdersByStaffId(int staffId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_header WHERE staff_id = ? ORDER BY order_time";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, staffId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int tableId = rs.getInt("table_id");
                LocalDateTime orderTime = rs.getTimestamp("order_time").toLocalDateTime();
                BigDecimal totalCost = rs.getBigDecimal("total_cost");
                String statusStr = rs.getString("status");
                orders.add(new Order(orderId, tableId, staffId, orderTime, totalCost, Model.OrderStatus.fromString(statusStr)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
}