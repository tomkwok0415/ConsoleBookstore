package com.bookstore;

import com.bookstore.models.Book;
import com.bookstore.models.Order;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class BookstoreOperations {
    private final DatabaseOperations databaseOperations;

    public BookstoreOperations(DatabaseOperations databaseOperations) {
        this.databaseOperations = databaseOperations;
    }

    public void bookstoreOperationMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Bookstore Operation =====");
            System.out.println("> 1. Order Update");
            System.out.println("> 2. Order Query");
            System.out.println("> 3. N Most Popular Books");
            System.out.println("> 4. Back to Main Menu");
            System.out.print(">>> Please enter your query: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        updateOrderStatus(scanner);
                        break;
                    case 2:
                        queryOrdersByShippingStatus(scanner);
                        break;
                    case 3:
                        queryMostPopularBooks(scanner);
                        break;
                    case 4:
                        System.out.println("Returning to main menu...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
                choice = 0;
            }
        } while (choice != 4);
    }

    public void updateOrderStatus(Scanner scanner) {
        System.out.print(">>> Enter the Order ID: ");
        String oid = scanner.nextLine();

        if (!databaseOperations.isOrderIdExists(oid)) {
            System.out.println("Order ID " + oid + " does not exist.");
            return;
        }

        printShippingStatusMenu();

        String sid;
        boolean validInput = false;

        do {
            sid = scanner.nextLine();
            if (sid.matches("[1-4]")) {
                validInput = true;
            } else {
                System.out.println("Invalid Shipping Status ID. Please enter a valid Shipping ID (1, 2, or 3).");
                System.out.print("Enter the Shipping Status ID: ");
            }
        } while (!validInput);
        if (sid.equals("4")) return;

        String currentStatus = databaseOperations.getOrderShippingStatus(oid);

        if (currentStatus.equals(sid)) {
            System.out.println("Failed to update the order. Shipping status is already set to " + databaseOperations.getShippingStatusNameById(sid) + ".");
            return;
        }

        int rowsAffected = databaseOperations.updateOrderStatus(oid, sid);

        if (rowsAffected > 0) {
            System.out.println("Order shipping status updated successfully.");
        } else {
            System.out.println("Failed to update the order.");
        }

        BookstoreSystem.pressEnterToContinue();
    }

    public void queryOrdersByShippingStatus(Scanner scanner) {
        printShippingStatusMenu();

        String sid;
        boolean validInput = false;

        do {
            sid = scanner.nextLine();
            if (sid.matches("[1-4]")) {
                validInput = true;
            } else {
                System.out.println("Invalid Shipping Status ID. Please enter a valid Shipping ID (1, 2, or 3).");
                System.out.print("Enter the Shipping Status ID: ");
            }
        } while (!validInput);
        if (sid.equals("4")) return;

        List<Order> orders = databaseOperations.queryOrdersByShippingStatus(sid);

        if (orders.size() > 0) {
            for (Order order : orders) {
                System.out.printf("Order ID: %s | Customer ID: %s | Customer Name: %s | Date: %s | ISBN: %s | Title: %s | Quantity: %d | Shipping Status: %s%n",
                        order.getOrderId(), order.getCustomerId(), order.getCustomerName(), order.getDate(), order.getIsbn(),
                        order.getBookTitle(), order.getQuantity(), order.getShippingStatus());
            }
        } else {
            System.out.println("No Order has been found.");
        }

        BookstoreSystem.pressEnterToContinue();
    }

    public void queryMostPopularBooks(Scanner scanner) {
        int n = 0;
        boolean validInput = false;

        do {
            try {
                System.out.print(">>> Enter the number of most popular books to display: ");
                n = scanner.nextInt();
                if (n <= 0) {
                    System.out.println("Invalid input. Please enter a positive integer.");
                } else {
                    validInput = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
            scanner.nextLine();
        } while (!validInput);

        List<Book> books = databaseOperations.queryMostPopularBooks(n);

        if (books.size() > 0) {
            for (Book book : books) {
                System.out.printf("ISBN: %s | Title: %s | Authors: %s | Price: %d | Inventory Quantity: %d | Total Ordered: %d%n",
                        book.getIsbn(), book.getTitle(), book.getAuthors(), book.getPrice(), book.getInventoryQuantity(), book.getTotalOrdered());
            }
        } else {
            System.out.println("No book has been found.");
        }

        BookstoreSystem.pressEnterToContinue();
    }

    private void printShippingStatusMenu() {
        System.out.println("\n===== Shipping Status ===== ");
        System.out.println("> 1 - ordered");
        System.out.println("> 2 - shipped");
        System.out.println("> 3 - received");
        System.out.println("> 4 - Back");
        System.out.print(">>> Enter the Shipping Status ID: ");
    }
}
