package com.bookstore;

import com.bookstore.models.Book;
import com.bookstore.models.Order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io. * ;

public class DatabaseOperations {
    private Connection databaseConnection;

    public DatabaseOperations(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection.getConnection();
        ResetDatabase();
    }

    Scanner scan = new Scanner(System.in);

    public void initDatabaseMenu() {
        //Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            printInitMenu();

            try {
                choice = scan.nextInt();
                scan.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1: CreateAllTables(); break;
                    case 2: DeleteAllRows(); break;
                    case 3: ResetDatabase(); break;
                    case 4: return;
                    default: System.out.println("[Error] Invalid operation, choose again.\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scan.nextLine(); // Clear the buffer
                choice = 0; // Reset choice
            }
        } while (choice != 5);
        scan.close();

    }

    public int getRecordCount(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;

        try (Statement stmt = databaseConnection.createStatement()) {
            ResultSet result = stmt.executeQuery(sql);
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void printInitMenu() {
        System.out.println("\n\n\n\n-----Operations for Database Initialization menu-----");
        System.out.println("> 1. Create all tables");
        System.out.println("> 2. Delete all rows");
        System.out.println("> 3. Reset Database");
        System.out.println("> 4. Back to Main Menu");
        System.out.print(">>> Please Enter Your Query: ");
    }

    private void CreateAllTables() {
        try (Statement stmt = databaseConnection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Shipping_Status (sid CHAR(3), status CHAR(20), PRIMARY KEY (sid))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Customers (uid CHAR(10) NOT NULL, name CHAR(50) NOT NULL, address CHAR(200) NOT NULL, PRIMARY KEY (uid))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Books (isbn CHAR(13), title CHAR(100) NOT NULL, authors CHAR(50) NOT NULL, price INTEGER UNSIGNED, inventory_quantity INTEGER UNSIGNED, PRIMARY KEY (isbn))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Orders (id INTEGER UNSIGNED AUTO_INCREMENT, oid CHAR(8) NOT NULL, uid CHAR(10) NOT NULL, date DATE, isbn CHAR(13) NOT NULL, quantity INTEGER UNSIGNED, shipping_status CHAR(3) NOT NULL, PRIMARY KEY (id), FOREIGN KEY (uid) REFERENCES Customers(uid), FOREIGN KEY (isbn) REFERENCES Books(isbn), FOREIGN KEY (shipping_status) REFERENCES Shipping_Status(sid))");

            stmt.execute("INSERT INTO Shipping_Status (sid, status) VALUES ('1', 'ordered'), ('2', 'shipped'), ('3', 'received') ON DUPLICATE KEY UPDATE sid=sid");
        } catch (SQLException e) {
            System.out.println("An error occurred while creating the tables: " + e.getMessage());
        }
    }

    private void DeleteAllRows() {
        try (Statement stmt = databaseConnection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Orders");
            stmt.execute("DROP TABLE IF EXISTS Books");
            stmt.execute("DROP TABLE IF EXISTS Shipping_Status");
            stmt.execute("DROP TABLE IF EXISTS Customers");
        } catch (SQLException e) {
            System.out.println("An error occurred while deleting the rows: " + e.getMessage());
        }

        CreateAllTables();
    }

    private void LoadDataFromFile() {
        String folderPath ="local_datafiles";

        InsertBooks(folderPath);
        InsertCustomers(folderPath);
        InsertOrders(folderPath);
    }

    private void ResetDatabase() {
        DeleteAllRows();
        LoadDataFromFile();
    }

    private void InsertBooks(String folderPath){
        try {
            String file_books = "";
            file_books = folderPath + "/books.csv";
			BufferedReader lineReader = new BufferedReader(new FileReader(file_books));
            String lineText = null;
 
            try {
                lineReader.readLine();
                String sql = "INSERT INTO Books (isbn, title, authors, price, inventory_quantity) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement predstmt;
                try {
                    predstmt = databaseConnection.prepareStatement(sql);
                    while ((lineText = lineReader.readLine()) != null) {
                        String[] data = lineText.split(",");
                        String isbn = data[0];
                        String title = data[1];
                        String authors = "";
                        int price = 0;
                        int inventory_quantity = 0;
    
                        int i =2;
                        if (data[2].startsWith("\"")) {
                            
                            while (data[i-1].endsWith("\"") != true){
                                authors += data[i] + ",";
                                i++;
                            }
                            authors = authors.replace("\"","");
                            price = Integer.parseInt(data[i]);
                            inventory_quantity = Integer.parseInt(data[i+1]);
                        } else {
                            authors = data[2];
                            price = Integer.parseInt(data[3]);
                            inventory_quantity = Integer.parseInt(data[4]);
                        }
         
                        predstmt.setString(1, isbn);
                        predstmt.setString(2, title);
                        predstmt.setString(3, authors);
                        predstmt.setInt(4, price);
                        predstmt.setInt(5, inventory_quantity);

                        predstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while inserting the books data: " + e.getMessage());
                    e.printStackTrace();
                }
                lineReader.close();
            } catch (IOException e) {
                System.out.println("An error occurred while IO file: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    private void InsertCustomers(String folderPath){
        try {
            //File csvFile = new File("C:/Users/user/Desktop/CSCI3170/BookStore/books.csv");
            String file_customers = "";
            file_customers = folderPath + "/customers.csv";
			BufferedReader lineReader = new BufferedReader(new FileReader(file_customers));
            String lineText = null;
 
            try {
                lineReader.readLine();
                String sql = "INSERT INTO Customers (uid, name, address) VALUES (?, ?, ?)";
                PreparedStatement predstmt;
                try {
                    predstmt = databaseConnection.prepareStatement(sql);
                    while ((lineText = lineReader.readLine()) != null) {
                        String[] data = lineText.split(",",3);

                        String uid = data[0];
                        String name = data[1];
                        String address = data[2];
                        address = address.replace("\"","");
         
                        predstmt.setString(1, uid);
                        predstmt.setString(2, name);
                        predstmt.setString(3, address);

                        predstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while inserting the customers data: " + e.getMessage());
                }
                lineReader.close();
            } catch (IOException e) {
                System.out.println("An error occurred while IO file: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while opening file: " + e.getMessage());
        }
    }

    private void InsertOrders(String folderPath){
        try {
            String file_orders = "";
            file_orders = folderPath + "/orders.csv";
			BufferedReader lineReader = new BufferedReader(new FileReader(file_orders));
            String lineText = null;
 
            try {
                lineReader.readLine();
                String sql = "INSERT INTO Orders (oid, uid, date, isbn, quantity, shipping_status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement predstmt;
                try {
                    predstmt = databaseConnection.prepareStatement(sql);
                    while ((lineText = lineReader.readLine()) != null) {
                        String[] data = lineText.split(",");
                            
                        String oid = data[0];
                        String uid = data[1];
                        String date = data[2];
                        String isbn = data[3];
                        int quantity = Integer.parseInt(data[4]);
                        String shipping_status = data[5];
                            
                        predstmt.setString(1, oid);
                        predstmt.setString(2, uid);
                        predstmt.setObject(3, date);
                        predstmt.setString(4, isbn);
                        predstmt.setInt(5, quantity);
                        predstmt.setString(6, shipping_status);

                        predstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    System.out.println("An error occurred while inserting the orders data: " + e.getMessage());
                }
     
                lineReader.close();
            } catch (IOException e) {
                System.out.println("An error occurred while IO file: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while opening file: " + e.getMessage());
        }
    }

    public int updateOrderStatus(String oid, String sid) {
        int rowCount = 0;
        try {
            String query = "UPDATE Orders SET shipping_status = ? WHERE oid = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, sid);
            stmt.setString(2, oid);

            rowCount = stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("An error occurred while updating the order shipping status: " + e.getMessage());
        }

        return rowCount;
    }

    public String getOrderShippingStatus(String oid) {
        try {
            String query = "SELECT shipping_status FROM orders WHERE oid = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, oid);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("shipping_status");
            } else {
                throw new RuntimeException("Order with id " + oid + " does not exist.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getShippingStatusNameById(String sid) {
        try {
            String query = "SELECT status FROM shipping_status WHERE sid = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, sid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<Order> queryOrdersByShippingStatus(String sid) {
        List<Order> orders = new ArrayList<>();

        try {
            String query = "SELECT o.oid, o.uid, c.name, o.date, o.isbn, b.title, o.quantity, s.status " +
                    "FROM Orders o " +
                    "JOIN Customers c ON o.uid = c.uid " +
                    "JOIN Books b ON o.isbn = b.isbn " +
                    "JOIN Shipping_Status s ON o.shipping_status = s.sid " +
                    "WHERE s.sid = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, sid);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getString("oid"),
                        rs.getString("uid"),
                        rs.getString("name"),
                        rs.getDate("date"),
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("An error occurred while querying orders by shipping status: " + e.getMessage());
        }
        return orders;
    }

    public List<Book> queryMostPopularBooks(int n) {
        List<Book> books = new ArrayList<>();
        try {
            String query = "SELECT b.*, SUM(o.quantity) as total_ordered FROM Orders o INNER JOIN Books b ON o.isbn = b.isbn GROUP BY o.isbn ORDER BY total_ordered DESC LIMIT ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setInt(1, n);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("authors"),
                        rs.getInt("price"),
                        rs.getInt("inventory_quantity"),
                        rs.getInt("total_ordered")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            System.out.println("An error occurred while querying the most popular books: " + e.getMessage());
        }

        return books;
    }

    public boolean isOrderIdExists(String oid) {
        int count = 0;

        try {
            String query = "SELECT COUNT(*) AS count FROM orders WHERE oid = ?";
            PreparedStatement stmt = databaseConnection.prepareStatement(query);
            stmt.setString(1, oid);
            ResultSet rs = stmt.executeQuery();

            rs.next();
            count = rs.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count > 0;
    }

}
   
   



