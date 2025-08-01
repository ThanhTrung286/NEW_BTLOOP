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
