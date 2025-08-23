package com.NEW_BTLOOP.LibraryManagement;

public class Book extends Document {
    public String publisher; // Nhà xuất bản
    public int numberOfPages; // Số trang
    public String genre; // Thể loại
    public String ISBN; // Mã ISBN

    // Constructor
    public Book() {
        super(); // Gọi constructor của lớp cha Document
        this.publisher = "";
        this.numberOfPages = 0;
        this.genre = "";
        this.ISBN = "";
    }

    // getter, setter Publisher.
    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // getter, setter NumberOfPages.
    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    // getter, setter Genre.
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
}
