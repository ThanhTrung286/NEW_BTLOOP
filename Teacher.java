package NEW_BTLOOP;

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
}