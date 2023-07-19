package com.bookstore.models;

public class Book {
    private String isbn;
    private String title;
    private String authors;
    private int price;
    private int inventoryQuantity;
    private int totalOrdered;

    public Book(String isbn, String title, String authors, int price, int inventoryQuantity, int totalOrdered) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.price = price;
        this.inventoryQuantity = inventoryQuantity;
        this.totalOrdered = totalOrdered;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        this.inventoryQuantity = inventoryQuantity;
    }

    public int getTotalOrdered() {
        return totalOrdered;
    }

    public void setTotalOrdered(int totalOrdered) {
        this.totalOrdered = totalOrdered;
    }
}
