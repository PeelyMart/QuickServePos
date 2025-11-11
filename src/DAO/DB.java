package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

<<<<<<< Updated upstream:src/Model/DB.java
public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/testDB";
    private static final String USER = "root";
    private static final String PASS = "";
=======
public class Database {
    private static final String URL = System;
    private static final String USER = "root";
    private static final String PASS = System.getenv("DATABASE_PASS");
>>>>>>> Stashed changes:src/DAO/Database.java

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
        }
    }

