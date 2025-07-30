package packagechung;

public class Thesis extends Document {
    public String supervisor; // Người hướng dẫn
    public String university; // Trường đại học
    public String department; // Khoa

    // Constructor
    public Thesis() {
        super(); // Gọi constructor của lớp cha Document
        this.inputThesisInfo(); // Nhập thông tin "luận văn"
    }

    //getInfo tài liệu
    @Override
    public String getInfo() {
        return super.getInfo() + ", Người hướng dẫn: " + supervisor + ", Trường đại học: " + university + ", Khoa: " + department;
    }
    
    //getter, setter Supervisor.
    public String getSupervisor() {
        return supervisor;
    }
    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }
    //getter, setter University.
    public String getUniversity() {
        return university;
    }   
    public void setUniversity(String university) {
        this.university = university;
    }
    //getter, setter Department.
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

    /**
     * Nhập thông tin luận văn. Chức năng 1
     * @param None
     * @return void
     */
    public void addThesis() {
        addDocument(); // Gọi phương thức addDocument() để nhập thông tin chung

        System.out.println("Nhập người hướng dẫn: ");
        this.supervisor = read.nextLine();

        System.out.println("Nhập trường đại học: ");
        this.university = read.nextLine();

        System.out.println("Nhập khoa: ");
        this.department = read.nextLine();

        System.out.println("Nhập năm xuất bản: ");
        this.year = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()
    }

    /**
     * Cập nhật thông tin luận văn. Chức năng 3
     * @param None  
     * @return void
     */
    public void updateThesis() {
        updateDocument(); // Gọi phương thức updateDocument() để cập nhật thông tin chung

        System.out.println("Nhập người hướng dẫn mới: ");
        this.supervisor = read.nextLine();

        System.out.println("Nhập trường đại học mới: ");
        this.university = read.nextLine();

        System.out.println("Nhập khoa mới: ");
        this.department = read.nextLine();

        System.out.println("Nhập năm xuất bản mới: ");
        this.year = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()
    }
}