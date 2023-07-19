package com.bookstore;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BookstoreSystem {
    private final DatabaseConnection databaseConnection;
    private DatabaseOperations databaseOperations;
    private BookstoreOperations bookstoreOperations;
    private CustomerOperations customerOperations;

    public BookstoreSystem(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
        this.databaseOperations = new DatabaseOperations(databaseConnection);
        this.bookstoreOperations = new BookstoreOperations(this.databaseOperations);
        this.customerOperations = new CustomerOperations(databaseConnection, this.databaseOperations);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n\n===== Welcome to Book Ordering Management System =====");
            System.out.println("+ System Date: " + getCurrentDate());
            System.out.println("+ Database Records: " + databaseOperations.getRecordCount("Books") + " Books, " +
                    databaseOperations.getRecordCount("Customers") + " Customers, " + databaseOperations.getRecordCount("Orders") + " Orders");
            System.out.println("-----------------------------------------------------");
            System.out.println("> 1. Database Initialization");
            System.out.println("> 2. Customer Operation");
            System.out.println("> 3. Bookstore Operation");
            System.out.println("> 4. Quit");
            System.out.print(">>> Please Enter Your Query: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        databaseOperations.initDatabaseMenu();
                        break;
                    case 2:
                        customerOperations.start();
                        break;
                    case 3:
                        bookstoreOperations.bookstoreOperationMenu();
                        break;
                    case 4:
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
                choice = 0;
            }
        } while (choice != 4);
        scanner.close();
    }

    private String getCurrentDate() {
        LocalDateTime currentDate = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDate.format(formatter);
    }

    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        System.out.print("\n\n");
    }

}
