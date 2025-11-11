import java.util.Date;

public class StaffTracker {
    private int staffId;
    private Date date;
    private Date timeIn;
    private Date timeOut;
    private int sessionMinutes;

    // Constructor
    public StaffTracker() {
        
    }

    public StaffTracker(int staffId, Date date, Date timeIn, Date timeOut, int sessionMinutes) {
        this.staffId = staffId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.sessionMinutes = sessionMinutes;
    }

    // Getters
    public int getStaffId() {
        return staffId;
    }

    public Date getDate() {
        return date;
    }

    public Date getTimeIn() {
        return timeIn;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public int getSessionMinutes() {
        return sessionMinutes;
    }

    // Setters
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTimeIn(Date timeIn) {
        this.timeIn = timeIn;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public void setSessionMinutes(int sessionMinutes) {
        this.sessionMinutes = sessionMinutes;
    }
}
