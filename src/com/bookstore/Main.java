package com.bookstore;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection databaseConnection = new DatabaseConnection();
        BookstoreSystem bookstoreSystem = new BookstoreSystem(databaseConnection);
        bookstoreSystem.start();
    }
}
