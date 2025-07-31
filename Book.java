package NEW_BTLOOP;

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

    //getInfo tài liệu
    @Override
    public String getInfo() {
        return super.getInfo() + "\n" +
                "Nhà xuất bản: " + publisher + "\n" +
                "Số trang: " + numberOfPages + "\n" +
                "Thể loại: " + genre + "\n" +
                "Mã ISBN: " + ISBN;
    }

    //getter, setter Publisher.
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

    // kiểm tra thể loại
    //có cần không????
    public boolean isGenre(String genre) {
        return this.genre.equalsIgnoreCase(genre);
    }

    /**
     * Nhập thông tin sách. Chức năng 1
     * @param None
     * @return void
     */
    public void addBook() {
        addDocument(); // Gọi phương thức addDocument() để nhập thông tin chung

        System.out.println("Nhập mã ISBN: ");
        this.ISBN = read.nextLine();
        
        System.out.println("Nhập nhà xuất bản: ");
        this.publisher = read.nextLine();

        System.out.println("Nhập số trang: ");
        this.numberOfPages = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

        System.out.println("Nhập thể loại: ");
        this.genre = read.nextLine();
    }

    /**
     * Cập nhật thông tin sách. Chức năng 3
     * @param None  
     * @return void
     */
    public void updateBook() {
        updateDocument(); // Gọi phương thức updateDocument() để cập nhật thông tin chung

        System.out.println("Nhập mã ISBN mới: ");
        this.ISBN = read.nextLine();
        
        System.out.println("Nhập nhà xuất bản mới: ");
        this.publisher = read.nextLine();

        System.out.println("Nhập số trang mới: ");
        this.numberOfPages = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

        System.out.println("Nhập thể loại mới: ");
        this.genre = read.nextLine();
    }

    /** 
    * Hiển thị thông tin sách. Chức năng 5
    * @param None
    * @return void
    */
    public void displayBook() {
        System.out.println("Thông tin sách: \n" + getInfo());
    }
}
