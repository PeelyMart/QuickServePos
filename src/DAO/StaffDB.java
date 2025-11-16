package DAO;
import Model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDB{


    public static Staff findById(int id) {
        String query = "SELECT *  FROM staff WHERE staff_id = ?";

        try (Connection conn = DB.getConnection(); //joint try parameter these instances of a resource is closed once done with try block
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);//replaces the '?' with the id
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { //loads it into a staff class
                return new Staff(
                        rs.getInt("staff_id"),
                        rs.getInt("staff_pin"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("contact_number")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int findAmountOfStaff() {
        String query = "SELECT COUNT(*) AS total FROM staff";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<Staff> returnAllStaff(){
        String query = "SELECT * from staff";
        List<Staff> staffs = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()){
                Staff currStaff = new Staff(
                        rs.getInt("staff_id"),
                        rs.getInt("staff_pin"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("contact_number"));
                staffs.add(currStaff);
            }
            return staffs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffs;
    }
}




