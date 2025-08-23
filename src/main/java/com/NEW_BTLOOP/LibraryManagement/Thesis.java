package com.NEW_BTLOOP.LibraryManagement;

public class Thesis extends Document {
    public String supervisor; // Người hướng dẫn
    public String university; // Trường đại học
    public String department; // Khoa

    // Constructor
    public Thesis() {
        super(); // Gọi constructor của lớp cha Document
        this.supervisor = "";
        this.university = "";
        this.department = "";
    }

    // getter, setter Supervisor.
    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    // getter, setter University.
    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    // getter, setter Department.
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    // getter, setter Year.
    public int getYear() {
        return super.year;
    }

    public void setYear(int year) {
        super.year = year;
    }

}