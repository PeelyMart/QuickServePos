import Model.Payment;
import DAO.PaymentDAO;

import java.time.LocalDateTime;
import java.util.*;

public class SalesReportController {

    /**
     * Returns totals and averages per payment method in a time range.
     * @param start Start date/time (inclusive)
     * @param end End date/time (inclusive)
     * @return A map: key = PaymentMethod, value = double array [total, average, count]
     */
    public static Map<Payment.PaymentMethod, double[]> getSalesByPaymentMethod(LocalDateTime start, LocalDateTime end) {
        Map<Payment.PaymentMethod, Double> totalMap = new HashMap<>();
        Map<Payment.PaymentMethod, Integer> countMap = new HashMap<>();
        PaymentDAO dao = new PaymentDAO();
        List<Payment> payments = dao.getAllPayments();

        for (Payment p : payments) {
            LocalDateTime payDate = p.getPaymentDate();
            if ((payDate.isBefore(start)) || (payDate.isAfter(end))) continue;
            Payment.PaymentMethod method = p.getPaymentMethod();
            double amount = p.getAmountPaid();

            totalMap.put(method, totalMap.getOrDefault(method, 0.0) + amount);
            countMap.put(method, countMap.getOrDefault(method, 0) + 1);
        }

        Map<Payment.PaymentMethod, double[]> report = new HashMap<>();
        for (Payment.PaymentMethod method : totalMap.keySet()) {
            double total = totalMap.get(method);
            int count = countMap.get(method);
            double avg = (count == 0) ? 0.0 : total / count;
            report.put(method, new double[]{total, avg, count});
        }
        return report;
    }

    // Example helpers to get start/end for day/month/year
    public static LocalDateTime getDayStart(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    public static LocalDateTime getDayEnd(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 23, 59, 59);
    }

    public static LocalDateTime getMonthStart(int year, int month) {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    public static LocalDateTime getMonthEnd(int year, int month) {
        int lastDay = java.time.YearMonth.of(year, month).lengthOfMonth();
        return LocalDateTime.of(year, month, lastDay, 23, 59, 59);
    }

    public static LocalDateTime getYearStart(int year) {
        return LocalDateTime.of(year, 1, 1, 0, 0);
    }

    public static LocalDateTime getYearEnd(int year) {
        return LocalDateTime.of(year, 12, 31, 23, 59, 59);
    }
}