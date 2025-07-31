package NEW_BTLOOP;

public class Librarian extends User {
    public String librarianID; // Mã thủ thư
    public String department; // Phòng ban

    // Constructor
    public Librarian() {
        super(); // Gọi constructor của lớp cha User

        System.out.println("Nhập mã thủ thư: ");
        librarianID = read.nextLine();

        System.out.println("Nhập phòng ban: ");
        department = read.nextLine();
    }

    //getInfo thủ thư
    @Override
    public String getInfo() {
        return super.getInfo() + ", Mã thủ thư: " + librarianID + ", Phòng ban: " + department;
    }

    //getter, setter LibrarianID.
    public String getLibrarianID() {
        return librarianID;
    }
    public void setLibrarianID(String librarianID) {
        this.librarianID = librarianID;
    }

    //getter, setter Department.
    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
}

