package Controller;

public class AuditReportTest {
    public static void main(String[] args) {
        AuditReportController ctrl = new AuditReportController();
        ctrl.printAuditReportForStaff(1234);  
        ctrl.printAuditReportForStaff(5678);
        ctrl.printAuditReportForStaff(9012);
        ctrl.printAuditReportForStaff(3456);
    }
}
