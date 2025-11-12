package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDB {



    public static void openOrder(){

        String stmt = "INSERT INTO order_header(table_id, staff_id, total_cost, status) VALUES (?, ?, 0.00, 'open')";

        try(Connection conn = DB.getConnection();
            PreparedStatement stmt = conn.prepareStatement() ) {



        }catch(SQLException e){
            e.printStackTrace();
        }
    }

}
