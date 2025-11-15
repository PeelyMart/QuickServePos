package Controller;

import DAO.LoyaltymemberDAO;
import java.time.LocalDate;
import java.util.Map;

public class LoyaltyReportController {
    private final LoyaltymemberDAO dao;

    public LoyaltyReportController(){
        this.dao = new LoyaltymemberDAO();
    }

    /**
    *  returns number of new members by month
    */
    public Map<LocalDate, Integer> getNewMembersByMonth (LocalDate start, LocalDate end) {
        return dao.countNewMembersByMonth(start, end);
    }

    /**
    * returns number of new members by year 
    */
    public Map<LocalDate, Integer> getNewMemberByYear (LocalDate start, LocalDate end) {
        return dao.countNewMembersByYear(start, end);
    }
}