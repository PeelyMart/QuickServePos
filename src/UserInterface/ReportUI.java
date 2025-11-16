package UserInterface;

import Controller.AuditReportController;
import DAO.StaffDB;
import Model.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.util.List;

public class ReportUI {

    @FXML
    private TextArea reportTextArea;

    @FXML
    private Button staffButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button loyaltyButton;

    @FXML
    private Button salesButton;

    @FXML
    private ComboBox<String> staffDropdown;

    @FXML
    public void initialize() {


        List<Staff> staffList = StaffDB.returnAllStaff();
        for (Staff s : staffList) {
            staffDropdown.getItems().add(s.getStaffId() + " - " + s.getFirstName());
        }

        staffDropdown.setOnAction(e -> {
            String selected = staffDropdown.getValue();
            if (selected == null) return;

            int staffId = Integer.parseInt(selected.split(" - ")[0]);

            String report = AuditReportController.generateAuditReportForStaff(staffId);
            reportTextArea.setText(report);
        });


        staffButton.setOnAction(e -> {
            staffDropdown.setVisible(true);
            reportTextArea.clear();  // optional
        });


        menuButton.setOnAction(e -> {
            staffDropdown.setVisible(false);
            reportTextArea.setText("MENU REPORT...");
        });

        loyaltyButton.setOnAction(e -> {
            staffDropdown.setVisible(false);
            reportTextArea.setText("MENU REPORT...");
        });

        salesButton.setOnAction(e -> {
            staffDropdown.setVisible(false);
            reportTextArea.setText("MENU REPORT...");
        });
    }

    private void loadStaffReport() {

        StringBuilder allReports = new StringBuilder();
        for (int i = 0; i < StaffDB.findAmountOfStaff(); i++) {
            String report = AuditReportController.generateAuditReportForStaff(i);
            allReports.append(report).append("\n\n"); // add spacing between reports
        }
        reportTextArea.setText(allReports.toString());

    }

    private void loadMenuReport() {
        reportTextArea.setText("MENU REPORT:\n\n- Menu Item A\n- Menu Item B\n- Menu Item C");
    }

    private void loadLoyaltyReport() {
        reportTextArea.setText("LOYALTY PROGRAM REPORT:\n\n- Member 1\n- Member 2\n- Member 3");
    }

    private void loadSalesReport() {
        reportTextArea.setText("SALES REPORT:\n\nTotal Sales: ₱50,000\nTransactions: 124");
    }
}
