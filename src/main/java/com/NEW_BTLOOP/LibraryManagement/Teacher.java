package com.NEW_BTLOOP.LibraryManagement;


public class Teacher extends User {
    public String teacherID; // Mã giáo viên
    public String degree; // Bằng cấp

    // Constructor
    public Teacher() {
        super(); // Gọi constructor của lớp cha User

        System.out.println("Nhập mã giáo viên: ");
        teacherID = read.nextLine();

        System.out.println("Nhập bằng cấp: ");
        degree = read.nextLine();
    }

    //getInfo giáo viên
    @Override
    public String getInfo() {
        return super.getInfo() + ", Mã giáo viên: " + teacherID + ", Bằng cấp: " + degree;
    }

    //getter, setter TeacherID.
    public String getTeacherID() {
        return teacherID;
    }
    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    //getter, setter Degree.
    public String getDegree() {
        return degree;
    }
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
     @Override
    // Mượn tài liệu nếu còn trong giới hạn
    public void borrowDocument(int amount) {
        if (borrowedDocuments + amount <= borrowedLimit) {
            borrowedDocuments += amount;
            System.out.println("Bạn đã mượn thành công: " + amount + " tài liệu.");
        } else {
            System.out.println("Không thể mượn thêm. Vượt quá giới hạn!");
        }
    }

    @Override
    // Trả tài liệu
    public void returnDocument(int amount) {
        if (borrowedDocuments - amount >= 0) {
            borrowedDocuments -= amount;
            System.out.println("Bạn đã trả thành công: " + amount + " tài liệu.");
        } else {
            System.out.println("Số lượng trả không hợp lệ.");
        }
    }
}
