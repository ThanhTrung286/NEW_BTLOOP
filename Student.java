package NEW_BTLOOP;

public class Student extends User {
    public String studentID; // Mã sinh viên

    // Constructor
    public Student() {
        super(); // Gọi constructor của lớp cha User

        System.out.println("Nhập mã sinh viên: ");
        studentID = read.nextLine();
    }

    //getInfo sinh viên
    @Override
    public String getInfo() {
        return super.getInfo() + ", Mã sinh viên: " + studentID;
    }

    //getter, setter StudentID.
    public String getStudentID() {
        return studentID;
    }
    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
}