package com.bookstore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;



public class CustomerOperations {
    private final Connection databaseConnection;
    private final DatabaseOperations databaseOperations;

    public CustomerOperations(DatabaseConnection databaseConnection, DatabaseOperations databaseOperations) {
        this.databaseConnection = databaseConnection.getConnection();
        this.databaseOperations = databaseOperations;
        //start();   
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {  
            System.out.println("\n-----------------------------------------------------");
            System.out.println("Customer Operations");         
            System.out.println("-----------------------------------------------------");
            System.out.println("> 1. Book Search");
            System.out.println("> 2. Place an Order");
            System.out.println("> 3. Check History Orders");
            System.out.println("> 4. Back to Main Menu");
            System.out.println("> 5. Quit");    
            System.out.print(">>> Please Enter Your Query: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1:
                        BookSearchOperations();
                        break;
                    case 2:
                        placeAnOrderOperations();
                        break;
                    case 3:
                        CheckHistoryOrdersOpeartions();
                        break;
                    case 4:
                        //System.out.println("Back to Main");
                        return;
                        //break;      
                    case 5:
                        System.exit(0);                                            
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the buffer
                choice = 0; // Reset choice
            }
        } while (choice != 4);
        //scanner.close();
    }

    private void BookSearchOperations() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("-----------------------------------------------------");
            System.out.println("Please select a query to search");           
            System.out.println("-----------------------------------------------------");
            System.out.println("> 1. ISBN");
            System.out.println("> 2. Book Title");
            System.out.println("> 3. Author Name");
            System.out.println("> 4. Back");
            System.out.print(">>> Please Enter Your Query: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer
                
                switch (choice) {
                    case 1:
                        BookSearch("ISBN");
                        break;
                    case 2:                       
                        BookSearch("Book Title");
                        break;
                    case 3:                      
                        BookSearch("Author name");  
                        break;
                    case 4:
                        //start();                        
                        return;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Clear the buffer
                choice = 0; // Reset choice
            }
        } while (choice != 4);
        //scanner.close();
    }

    private void BookSearch(String SearchType){
        System.out.println("-----------------------------------------------------");
        System.out.print("Please input " + SearchType + ":");                   

        Scanner scanner = new Scanner(System.in);
        String targert = scanner.nextLine();

        if (SearchType.equals("ISBN")) SearchType = "isbn";
        else if (SearchType.equals("Book Title")) SearchType = "title";
        else if (SearchType.equals("Author name")) SearchType ="authors";        

        String query;
        if (SearchType.equals("authors"))
            query = "SELECT * FROM books WHERE "+ SearchType +" Like '%"+ targert +",%';";
        else
            query = "SELECT * FROM books WHERE "+ SearchType +"= '"+ targert +"';";

        try (Statement stmt = databaseConnection.createStatement()) {            
            ResultSet rs = stmt.executeQuery(query);
            List<String> resultList = new ArrayList<>();

            while (rs.next()) {            
                String resultRow = "";
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resultRow += rs.getMetaData().getColumnName(i) + ":" +rs.getString(i) + " | ";
                }
                resultList.add(resultRow);
            }
            String resultString = String.join("\n", resultList);

            System.out.println("-----------------------------------------------------");
            System.out.println("Book Search Result:");
            System.out.println("-----------------------------------------------------");
            System.out.println(resultString);

            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BookstoreSystem.pressEnterToContinue();
    }

     private void placeAnOrderOperations(){
        System.out.println("-----------------------------------------------------");
        System.out.print("Please provide your Customer id (CID): ");
        Scanner scanner = new Scanner(System.in);
        String cid = scanner.nextLine();
        
        try (Statement stmt = databaseConnection.createStatement()) {
            String query = "SELECT * from customers WHERE uid = '" + cid +"'";
            //System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            if (!rs.next()) 
            {
                System.out.println("Fail");
                System.out.println("Reason: Customer not exist. Wrong CID!"); //check if result empty 
                //start();
                return;
            }
            String[] books = null;
            int[] books_quantity = null;
            rs.close();
            stmt.close();            
            AddToCart(books, cid, books_quantity);        
            //scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        

    private void showallbooks() {
        String query = "select * from books;";

        try (Statement stmt = databaseConnection.createStatement()) {            
            ResultSet rs = stmt.executeQuery(query);
            List<String> resultList = new ArrayList<>();        
            while (rs.next()) {            
                String resultRow = "";
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resultRow += rs.getMetaData().getColumnName(i) + ":" +rs.getString(i) + " | ";
                }
                resultList.add(resultRow);
            }
            String resultString = String.join("\n", resultList);

            System.out.println("-----------------------------------------------------");
            System.out.println("Books:");
            System.out.println("-----------------------------------------------------");
            System.out.println(resultString);
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }     

    }

    private void PrintCart(String[] books, int[] books_quantity) {

        if (books == null)
        {
            System.out.println("total books: 0");
        }
        else
        {
            System.out.println("total books: "+ Integer.toString(books.length));
            for(int i=0; i<books.length; i++)
                System.out.println("book isbn: " + books[i] + " quantity: " + Integer.toString(books_quantity[i]));
        }
    }
    
    private void AddToCart(String[] books, String cid , int[] books_quantity) {
        
        System.out.println("-----------------------------------------------------");
        System.out.println("Shopping Cart");            
        System.out.println("-----------------------------------------------------");       
        PrintCart(books, books_quantity);        
        System.out.println("-----------------------------------------------------");
        System.out.print("Please input the ISBN of the book you want to add to cart: ");
                
        Scanner scanner = new Scanner(System.in);
        String isbn  = scanner.nextLine();

        
        try (Statement stmt = databaseConnection.createStatement()) {
            while(true)
            {
                String query = "select * from books where isbn = '"+ isbn +"';";   
                //System.out.println(query);
                ResultSet rs = stmt.executeQuery(query);
                if (!rs.next()) 
                {
                    System.out.println("Fail");
                    System.out.println("Reason: Book not exist. Wrong ISBN!"); //check if result empty 
                    //scanner.close();
                    return;
                }
                List<String> resultList = new ArrayList<>();
                int inventory_quantity = rs.getInt("inventory_quantity");
                rs = stmt.executeQuery(query); //as we already rs.next once before (line 217: if (!rs.next())), pointer move
                while (rs.next()) {  
                    String resultRow = "";
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {                    
                        resultRow += rs.getMetaData().getColumnName(i) + ":" +rs.getString(i) + " | ";
                    }
                    resultList.add(resultRow);
                } 
                String resultString = String.join("\n", resultList);
                System.out.println("-----------------------------------------------------");
                System.out.println("Book Details: ");
                System.out.println("-----------------------------------------------------");
                System.out.println(resultString);
                System.out.println("-----------------------------------------------------");
                if(inventory_quantity == 0) 
                {
                    System.out.println("Fail");
                    System.out.println("Reason:: The inventory quatity of this book is 0");
                    return;
                }                
                System.out.println("Reminder: The inventory quantity of this book is " + inventory_quantity);
                System.out.println("Your input - inventory quantity should larger than or equal to 0");
                System.out.print("Please input your quantity of this book: ");
                int quantity = scanner.nextInt();
                scanner.nextLine();
                if (inventory_quantity - quantity >= 0 && quantity !=0) 
                {
                    String[] newbooks = null;
                    int[] newbooks_quantity = null;
                    if (books == null)
                    {
                        newbooks = new String[1];
                        newbooks[0] = isbn;                        
                        newbooks_quantity = new int[1];
                        newbooks_quantity[0] = quantity;
                        
                    }
                    else
                    {
                        boolean contain = false;
                        int index = 0;
                        for(int j=0;j<books.length; j++) {         
                            if(books[j].equals(isbn)) {
                                contain = true;   
                                index = j;
                            }
                            
                        }

                        if (contain) { //update cart
                            newbooks = books;
                            newbooks_quantity = books_quantity;
                            newbooks_quantity[index] = quantity;
                        }else { //add to cart
                            newbooks = new String[books.length +1];
                            int i;
                            for(i=0; i< books.length; i++)
                                newbooks[i] = books[i];
                            newbooks[i] = isbn;
                            newbooks_quantity = new int[books_quantity.length +1];                                    
                            for(i=0; i< books.length; i++)
                                newbooks_quantity[i] = books_quantity[i];
                            newbooks_quantity[i] = quantity;
                        }

                        
                    }
                    
                    int choice;
                    do {                                   
                        System.out.println("-----------------------------------------------------");
                        System.out.println("> 1. Checkout"); 
                        System.out.println("> 2. Continue to add");
                        System.out.println("> 3. Show Cart");                         
                        System.out.println("> 4. Clear Cart");
                        System.out.println("> 5. Back");                             
                        System.out.println("> 6. Quit");                    
                        System.out.print(">>> Please Enter Your Query: ");

                        try {
                            choice = scanner.nextInt();
                            scanner.nextLine(); // Clear the buffer
                            switch (choice) {
                                case 1: 
                                    Checkout(newbooks, cid, newbooks_quantity);
                                    return;
                                case 2:           
                                    //AddToCart(newbooks, cid, newbooks_quantity);
                                    books = newbooks;
                                    books_quantity = newbooks_quantity;
                                    System.out.println("-----------------------------------------------------");
                                    System.out.println("Shopping Cart");            
                                    System.out.println("-----------------------------------------------------");       
                                    PrintCart(books, books_quantity);        
                                    System.out.println("-----------------------------------------------------");
                                    System.out.print("Please input the ISBN of the book you want to add to cart: ");
                                    isbn  = scanner.nextLine();
                                    break;
                                case 3:
                                    System.out.println("-----------------------------------------------------");
                                    System.out.println("Shopping Cart");            
                                    System.out.println("-----------------------------------------------------"); 
                                    PrintCart(newbooks, newbooks_quantity);
                                    System.out.println("-----------------------------------------------------");
                                    break;
                                case 4:
                                    books = null;
                                    books_quantity = null;  
                                    newbooks = null;
                                    newbooks_quantity = null;                      
                                    System.out.println("-----------------------------------------------------");
                                    System.out.println("Shopping Cart");            
                                    System.out.println("-----------------------------------------------------"); 
                                    PrintCart(books, books_quantity);
                                    break;
                                case 5:     //If select select 2 before, these will return back in 2 (AddToCart) but remove last book that added to cart                                               
                                    return;  // If no , it will back to (SelectBook) last page, with no book
                                case 6:
                                    System.exit(0);                                                                          
                                default:
                                    System.out.println("Invalid choice! Please try again.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input! Please enter a number.");
                            scanner.nextLine(); // Clear the buffer
                            choice = 0; // Reset choice
                        }
                    } while (choice !=2 && choice != 5 && choice != 6); //while (!exitflag);         
                    //scanner.close();           
                    //return;                      
                }
                else {
                    System.out.println("order fail:invalid quantity");                    
                }
            }
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
                
    }

    private void Checkout(String[] books, String cid , int[] books_quantity) {
        int i;

        Random rand = new Random();
        String oid = "";
        do {
            int n = rand.nextInt(100000000);
            oid = String.format("%08d", n);
        } while (databaseOperations.isOrderIdExists(oid));

        for(i=0; i<books.length; i++) {
            String query = "select * from books where isbn = '"+ books[i] +"';";
            try (Statement stmt = databaseConnection.createStatement())
            {
                ResultSet rs = stmt.executeQuery(query); 
                rs.next();
                int inventory_quantity = rs.getInt("inventory_quantity");
                if(inventory_quantity-books_quantity[i] < 0)
                {
                    System.out.println("Fail");
                    System.out.println("Reason: There is inventory shortage of book with isbn "+ books[i]);
                    System.out.println("There may be concurrency problem");
                    return;
                }

                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String datetime = currentDate.format(formatter);
                //java.sql.Date date = java.sql.Date.valueOf(datetime);
                
                String quantity = Integer.toString(books_quantity[i]);
                
                query = "INSERT INTO orders (oid, uid, date, isbn, quantity, shipping_status) VALUES ('"+oid+"', '"+cid+"', '"+datetime+"', '"+books[i]+"', "+quantity+", '1');";

                //System.out.println(query);
                stmt.execute(query); 

                int newInventoryQuantity = inventory_quantity - books_quantity[i];                
                query = "UPDATE books SET inventory_quantity = " + newInventoryQuantity + " WHERE isbn = '" + books[i] + "';";
                //System.out.println(query);
                stmt.execute(query);

                System.out.println("Success");
                System.out.println("Placed a order of book with ISBN" + books[i]);   
                UpdateOrderState(oid);
            }catch (SQLException e) {
                e.printStackTrace();
            }

        }
        //start();
        return;
    }

    public void CheckHistoryOrdersOpeartions() {
        System.out.println("-----------------------------------------------------");
        System.out.print("Please provide your Customer id (CID): ");
        Scanner scanner = new Scanner(System.in);
        String cid = scanner.nextLine();

        try {
            String query = "SELECT o.oid, o.date, s.status, b.title, o.quantity, b.price * o.quantity AS total_price, b.isbn " +
                    "FROM Orders o " +
                    "JOIN Shipping_Status s ON o.shipping_status = s.sid " +
                    "JOIN Books b ON o.isbn = b.isbn " +
                    "WHERE o.uid = ? " +
                    "ORDER BY o.date DESC";

            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, cid);
            ResultSet rs = stmt.executeQuery();

            System.out.println("-----------------------------------------------------");
            System.out.println("Order History:");
            System.out.println("-----------------------------------------------------");

            while (rs.next()) {
                String orderId = rs.getString("oid");
                Date orderDate = rs.getDate("date");
                String shippingStatus = rs.getString("status");
                String bookTitle = rs.getString("title");
                int quantity = rs.getInt("quantity");
                int totalPrice = rs.getInt("total_price");
                String isbn = rs.getString("isbn");

                System.out.printf("Order ID: %s | Order Date: %s | Shipping Status: %s | Book Title: %s | Quantity: %d | Total Price: %d | ISBN: %s%n",
                        orderId, orderDate.toString(), shippingStatus, bookTitle, quantity, totalPrice, isbn);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving order history: " + e.getMessage());
        }

        BookstoreSystem.pressEnterToContinue();
    }

    private void UpdateOrderState(String oid)
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                //System.out.println("Task executed after 30 seconds " + oid);
                try(Statement stmt = databaseConnection.createStatement()) 
                {
                    String query = "UPDATE orders SET shipping_status = '2' WHERE oid = '" + oid + "';";
                    //System.out.println(query);
                    stmt.execute(query);
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 30000);
    }
}
