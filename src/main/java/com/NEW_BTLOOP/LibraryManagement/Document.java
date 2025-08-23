package com.NEW_BTLOOP.LibraryManagement;

import java.util.Scanner;

public class Document {
    public String title;
    public String author;
    public String ID; // Mã tài liệu
    public int year;
    public int total; // Tổng số sách tv có
    public int avail; // Số sách cho mượn được
    public int borrowed; // Số sách đã mượn

    // constructor
    public Document() {
        this.title = "";
        this.author = "";
        this.ID = "";
        this.year = 0;
        this.total = 0;
        this.avail = 0;
        this.borrowed = 0;
    }

    public void setTitle(String title) {

        this.title = title;

    }

    public String getTitle() {

        return title;

    }

    public void setAuthor(String author) {

        this.author = author;

    }

    public String getAuthor() {

        return author;

    }

    public void setId(String ID) {

        this.ID = ID;

    }

    public String getId() {

        return ID;

    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setAvail(int avail) {
        this.avail = avail;
    }

    public int getAvail() {
        return avail;
    }

}
