package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class stafftrackerDAO {

    public boolean addStaffTracker(StaffTracker tracker) {
        String sql = "INSERT INTO staff_tracker (staff_id, date, time_in, time_out, session_minutes) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tracker.getStaffId());
            stmt.setDate(2, new java.sql.Date(tracker.getDate().getTime()));

            // time_in and time_out may be null
            if (tracker.getTimeIn() != null) {
                stmt.setTimestamp(3, new Timestamp(tracker.getTimeIn().getTime()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            if (tracker.getTimeOut() != null) {
                stmt.setTimestamp(4, new Timestamp(tracker.getTimeOut().getTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setInt(5, tracker.getSessionMinutes());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public StaffTracker getStaffTrackerById(int staffId, Date date) {
        String sql = "SELECT * FROM staff_tracker WHERE staff_id = ? AND date = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Date timeIn = new Date(rs.getTime("time_in").getTime());
                Date timeOut = new Date(rs.getTime("time_out").getTime());
                int sessionMinutes = rs.getInt("session_minutes");

                return new StaffTracker(staffId, date, timeIn, timeOut, sessionMinutes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateStaffTracker(StaffTracker tracker) {
        String sql = "UPDATE staff_tracker SET time_in = ?, time_out = ?, session_minutes = ? WHERE staff_id = ? AND date = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (tracker.getTimeIn() != null) {
                stmt.setTimestamp(1, new Timestamp(tracker.getTimeIn().getTime()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }

            if (tracker.getTimeOut() != null) {
                stmt.setTimestamp(2, new Timestamp(tracker.getTimeOut().getTime()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }

            stmt.setInt(3, tracker.getSessionMinutes());
            stmt.setInt(4, tracker.getStaffId());
            stmt.setDate(5, new java.sql.Date(tracker.getDate().getTime()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteStaffTracker(int staffId, Date date) {
        String sql = "DELETE FROM staff_tracker WHERE staff_id = ? AND date = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, staffId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<StaffTracker> getAllStaffTrackers() {
        ArrayList<StaffTracker> trackers = new ArrayList<>();
        String sql = "SELECT * FROM staff_tracker ORDER BY staff_id, date";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int staffId = rs.getInt("staff_id");
                Date date = rs.getDate("date");
                Date timeIn = new Date(rs.getTime("time_in").getTime());
                Date timeOut = new Date(rs.getTime("time_out").getTime());
                int session_minutes = rs.getInt("session_minutes");

                trackers.add(new StaffTracker(staffId, date, timeIn, timeOut, session_minutes));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackers;
    }
}
