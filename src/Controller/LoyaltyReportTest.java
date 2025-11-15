package Controller;

import java.time.LocalDate;
import java.util.Map;

public class LoyaltyReportTest {
    public static void main(String[] args) {
        LoyaltyReportController controller = new LoyaltyReportController();

        // test new members by month
        LocalDate startMonth = LocalDate.of(2024, 1, 1);
        LocalDate endMonth = LocalDate.of(2024, 1, 31);
        Map<LocalDate, Integer> monthlyNewMembers = controller.getNewMembersByMonth(startMonth, endMonth);
        System.out.println("New Members by Month:");
        monthlyNewMembers.forEach((date, count) -> 
            System.out.printf("%s: %d\n", date, count));

        // Test new members by year
        LocalDate startYear = LocalDate.of(2024, 1, 1);
        LocalDate endYear = LocalDate.of(2024, 12, 31);
        Map<LocalDate, Integer> yearlyNewMembers = controller.getNewMemberByYear(startYear, endYear);
        System.out.println("New Members by Year:");
        yearlyNewMembers.forEach((date, count) -> 
            System.out.printf("%s: %d\n", date, count));
    }
}