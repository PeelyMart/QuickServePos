package Controller;

import DAO.OrderDB;
import DAO.PaymentDAO;
import DAO.StaffDB;
import DAO.StaffTrackDAO;
import Model.Order;
import Model.Payment;
import Model.Staff;
import Model.StaffTracker;
import java.util.List;

public class AuditReportController {

    public static String generateAuditReportForStaff(int staffId) {
        StringBuilder sb = new StringBuilder();

        Staff staff = StaffDB.findById(staffId);
        if (staff == null) {
            return "No staff member found with the ID: " + staffId;
        }

        sb.append("STAFF REPORT FOR: ")
                .append(staff.getFirstName()).append(" ").append(staff.getLastName())
                .append("\n\n");

        // Sessions
        List<StaffTracker> sessions = StaffTrackDAO.getSessionsByStaffId(staffId);
        sb.append("SESSIONS:\n");
        sb.append(String.format("%-10s %-20s %-20s %-10s%n",
                "StaffID", "Login", "Logout", "Minutes"));

        for (StaffTracker tracker : sessions) {
            sb.append(String.format("%-10d %-20s %-20s %-10d%n",
                    tracker.getStaffId(),
                    tracker.getTimeIn(),
                    tracker.getTimeOut(),
                    tracker.getSessionMinutes()));
        }

        // Orders
        List<Order> orders = OrderDB.getOrdersByStaffId(staffId);
        sb.append("\nORDERS PROCESSED:\n");
        sb.append(String.format("%-10s %-20s %-10s%n", "OrderID", "Order Time", "Total"));

        for (Order order : orders) {
            sb.append(String.format("%-10d %-20s %-10.2f%n",
                    order.getOrderId(),
                    order.getOrderTime(),
                    order.getTotalCost() != null ? order.getTotalCost() : 0.0));
        }

        // Payments
        List<Payment> payments = PaymentDAO.getPaymentsByStaffId(staffId);
        sb.append("\nPAYMENTS PROCESSED:\n");
        sb.append(String.format("%-10s %-12s %-20s %-10s%n", "PayID", "Amount", "Date", "Method"));

        for (Payment payment : payments) {
            sb.append(String.format("%-10d %-12.2f %-20s %-10s%n",
                    payment.getTransactionId(),
                    payment.getAmountPaid(),
                    payment.getPaymentDate(),
                    payment.getPaymentMethod()));
        }

        return sb.toString();
    }
}
