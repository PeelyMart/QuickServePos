package testers;

import DAO.StaffDB;
import Model.Staff;

import java.util.List;

public class ListTest {
    public static void main(String[] args){
        List<Staff> currList = StaffDB.returnAllStaff();
        for(Staff s :currList){
            System.out.println(s.getStaffId() + " " + s.getFirstName());
        }

    }
}
