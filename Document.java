package NEW_BTLOOP;

import java.util.Scanner;

public class Document {
	public String title;
	public String author;
	public String ID; // Mã tài liệu
	public int year;
	public int total; //Tổng số sách tv có
	public int avail; //Số sách cho mượn được
    public int borrowed; // Số sách đã mượn

    // Đối tượng Scanner để đọc dữ liệu từ bàn phím
	protected Scanner read = new Scanner(System.in);

	//constructor
	public Document() {
        this.title = "";
        this.author = "";
        this.ID = "";
        this.year = 0;
        this.total = 0;
        this.avail = 0;
        this.borrowed = 0;
	}

	//getInfo tài liệu
	public String getInfo() {
		return "Tiêu đề: " + title + "\n" +
                "Tác giả: " + author + "\n" +
                "Mã ID: " + ID + "\n" +
                "Năm xuất bản: " + year + "\n" +
                "Tổng số: " + total + "\n" +
                "Số lượng có sẵn: " + avail;
	}

	@Override
	public String toString() {
		return getInfo();
	}
	
    /**
     * Thêm tài liệu vào danh sách. Chức năng 1
     * @param type Loại tài liệu (Book/Thesis).
     * @return void
     */
    public void addDocument() {
        System.out.println("NHẬP THÔNG TIN TÀI LIỆU: ");

		System.out.println("Nhập mã ID: ");
		this.ID = read.nextLine();

		System.out.println("Nhập tên tài liệu: ");
		this.title = read.nextLine();

		System.out.println("Nhập tác giả: ");
		this.author = read.nextLine();

		System.out.println("Nhập tổng số: ");
		this.total = read.nextInt();
		read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

		System.out.println("Nhập năm xuất bản: ");
		this.year = read.nextInt();
		read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

		this.avail = this.total;
	}

    /**
     * Cập nhật thông tin tài liệu. Chức năng 3
     * @param None  
     * @return void
     */
    public void updateDocument() {
        System.out.println("Cập nhật thông tin tài liệu: ");
        System.out.println("Nhập tên tài liệu mới: ");
        this.title = read.nextLine();

        System.out.println("Nhập tác giả mới: ");
        this.author = read.nextLine();

        System.out.println("Nhập mã ID mới: ");
        this.ID = read.nextLine();

        System.out.println("Nhập năm xuất bản mới: ");
        this.year = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

        System.out.println("Nhập tổng số sách mới: ");
        this.total = read.nextInt();
        read.nextLine(); // Đọc ký tự newline còn lại sau nextInt()

        this.avail = this.total - this.borrowed; // Cập nhật số lượng có sẵn
    }

    /**
     * Tìm kiếm tài liệu. Chức năng 4
     * @param String keyword Từ khóa tìm kiếm
     * @return void
     */
    public void findDocument(String keyword) {
        // Kiểm tra xem từ khóa có trong tiêu đề, tác giả hoặc ID không
        if (this.title.toLowerCase().contains(keyword.toLowerCase()) || this.author.toLowerCase().contains(keyword.toLowerCase()) || this.ID.toLowerCase().contains(keyword.toLowerCase())) { 
            // In thông tin tài liệu theo TÊN
            System.out.println("Tài liệu tìm thấy theo tên:\n");
            for (Document doc : Library.documents) {
                if (doc.title.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println(doc.title + " - " + doc.author + " - " + doc.ID);
                }
            }
            // In thông tin tài liệu theo TÁC GIẢ
            System.out.println("Tài liệu tìm thấy theo tác giả:\n");
            for (Document doc : Library.documents) {
                if (doc.author.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println(doc.title + " - " + doc.author + " - " + doc.ID);
                }
            }
            // In thông tin tài liệu theo ID
            System.out.println("Tài liệu tìm thấy theo ID:\n");
            for (Document doc : Library.documents) {
                if (doc.ID.toLowerCase().contains(keyword.toLowerCase())) {
                    System.out.println(doc.title + " - " + doc.author + " - " + doc.ID);
                }
            }
        } else {
            System.out.println("Không tìm thấy tài liệu nào phù hợp.");
        }
    }

    /**
     * Hiển thị thông tin tài liệu + danh sách tài liệu. Chức năng 5
     * @param None
     * @return void
     */
    // Hiển thị danh sách tài liệu
    public void displayDocument() {
        System.out.println("Danh sách tài liệu: ");
        for (Document doc : Library.documents) {
            if (doc != null) {
                System.out.println(doc.title + " - " + doc.author + " - " + doc.ID );
            }
        }
    }

    /**
     * Mượn tài liệu. Chức năng 7
     * @param None  
     * @return void
     * Nếu tài liệu có sẵn, giảm số lượng có sẵn và tăng số lượng đã mượn
     */
    public void borrowDocument() {
        if (this.avail > 0) {
            this.avail--;
            this.borrowed++;
            System.out.println("Đã mượn tài liệu: " + this.title);
        } else {
            System.out.println("Tài liệu không còn sẵn để mượn.");
        }
    }

    /**
     * Trả tài liệu. Chức năng 8
     * @param None  
     * @return void
     * Tăng số lượng có sẵn và giảm số lượng đã mượn
     */
    public void returnDocument() {
        this.avail++;
        this.borrowed--;
        System.out.println("Đã trả tài liệu: " + this.title);
    }
}
