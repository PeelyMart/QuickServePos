package Controller;

import Model.Payment;
import DAO.PaymentDAO;

import java.time.LocalDateTime;
import java.util.Map;


public class PaymentTest {
    public static void main(String[] args) {

        // Generate report
        LocalDateTime start = SalesReportController.getDayStart(2025, 11, 15);
        LocalDateTime end = SalesReportController.getDayEnd(2025, 11, 15);
        Map<Payment.PaymentMethod, double[]> report = SalesReportController.getSalesByPaymentMethod(start, end);
        report.forEach((method, arr) -> 
            System.out.printf("%s: Total=%.2f Avg=%.2f Count=%.0f\n", method, arr[0], arr[1], arr[2]));
    }
}
