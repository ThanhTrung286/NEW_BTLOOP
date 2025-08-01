package com.NEW_BTLOOP.LibraryManagement;


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
