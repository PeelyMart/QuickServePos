import Controller.UserService;
import DAO.DB;
import Model.Staff;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class loginTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean programRunning = true;

        try (Connection conn = DB.getConnection()) {
            while (programRunning) {
                // Get the current user from UserService
                Staff currentUser = UserService.getCurrentUser();

                // Display current user status
                if (currentUser == null) {
                    System.out.println("\n\n\nCURRENT USER: Logged out");
                } else {
                    System.out.println("\n\n\nCURRENT USER: " + currentUser.getFirstName());
                }

                // Display menu
                System.out.println("Choose an option:");
                System.out.println("[1] Login");
                System.out.println("[2] Logout");
                System.out.println("[0] Exit");
                System.out.print("Input: ");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> { // Login
                        System.out.print("UserID: ");
                        int id = scanner.nextInt();
                        System.out.print("PIN: ");
                        int pin = scanner.nextInt();

                        int loginResult = UserService.logIn(id, pin);
                        if (loginResult == 0) {
                            System.out.println("You are logged in as " + UserService.getCurrentUser().getFirstName());
                        } else if (loginResult == 1) {
                            System.out.println("User not found.");
                        } else if (loginResult == 2) {
                            System.out.println("Incorrect PIN.");
                        }
                    }
                    case 2 -> { // Logout
                        UserService.logOut();
                        System.out.println("You have logged out.");
                    }
                    case 0 -> programRunning = false; // Exit
                    default -> System.out.println("Invalid option. Please choose 0, 1, or 2.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database:");
            e.printStackTrace();
        }
    }
}
