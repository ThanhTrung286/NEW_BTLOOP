package com.NEW_BTLOOP.LibraryManagement;


import java.util.Scanner;

public class User {
    public String name; // Tên người dùng
    public String email; // Email người dùng
    public String userID;
public int borrowedDocuments = 0; // Số tài liệu đã mượn
    public int borrowedLimit; // Giới hạn sách có thể mượn

    // Đọc đầu vào
    protected Scanner read = new Scanner(System.in);

    // Constructor
    public User() {
        System.out.println("Nhập tên người dùng: ");
        name = read.nextLine();

        System.out.println("Nhập email: ");
        email = read.nextLine();

        System.out.println("Nhập ID người dùng: ");
        userID = read.nextLine();

        System.out.println("Nhập giới hạn sách có thể mượn: ");
        borrowedLimit = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()
    }

    //getInfo người dùng
    public String getInfo() {
        return "Tên người dùng: " + name + ", Email: " + email + ", ID người dùng: " + userID + ", Số tài liệu đã mượn: " + borrowedDocuments + ", Giới hạn tài liệu có thể mượn: " + borrowedLimit;
    }

    @Override
    public String toString() {
        return getInfo();
    }
    
    //getter, setter Name.
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    //getter, setter Email.
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    //getter, setter UserID.
    public String getUserID() { 
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    //getter, setter BorrowedDocuments.
    public int getBorrowedDocuments() {
        return borrowedDocuments;
    }
    public void setBorrowedDocuments(int borrowedDocuments) {
        this.borrowedDocuments = borrowedDocuments;
    }

    //getter, setter BorrowedLimit.
    public int getBorrowedLimit() {
        return borrowedLimit;
    }
    public void setBorrowedLimit(int borrowedLimit) {
        this.borrowedLimit = borrowedLimit;
    }

    // Kiểm tra xem số tài liệu mượn có vượt quá giới hạn hay không
    public void borrowDocument(int amount) {
        if (borrowedDocuments + amount <= borrowedLimit) {
            borrowedDocuments += amount;
        } else {
            System.out.println("Số tài liệu mượn vượt quá giới hạn cho phép.");
        }
    }

    // Trả tài liệu
    public void returnDocument(int amount) {
        if (borrowedDocuments - amount >= 0) {
            borrowedDocuments -= amount;
        } else {
            System.out.println("Số tài liệu trả không hợp lệ.");
        }
    }
}