package NEW_BTLOOP;

import java.util.*;

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

     // Kiểm tra tài liệu trong thư viện theo từ khóa
    public void checkDocument(String keyword) {
        boolean found = false;

        for (Book book : Library.books) {
            if (book.title.toLowerCase().contains(keyword.toLowerCase()) ||
                    book.author.toLowerCase().contains(keyword.toLowerCase()) ||
                    book.ID.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("Tìm thấy SÁCH:");
                System.out.println(book.getInfo());
                found = true;
            }
        }

        for (Thesis thesis : Library.theses) {
            if (thesis.title.toLowerCase().contains(keyword.toLowerCase()) ||
                    thesis.author.toLowerCase().contains(keyword.toLowerCase()) ||
                    thesis.ID.toLowerCase().contains(keyword.toLowerCase())) {
                System.out.println("Tìm thấy LUẬN VĂN:");
                System.out.println(thesis.getInfo());
                found = true;
            }
        }

        if (!found) {
            System.out.println("Không tìm thấy tài liệu với từ khóa: " + keyword);
        }
    }

    // Thêm tài liệu mới bằng cách nhập thông tin
    public void addDocument() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Thêm loại tài liệu nào?");
        System.out.println("1. Sách");
        System.out.println("2. Luận văn");
        int type = scanner.nextInt();
        scanner.nextLine();

        if (type == 1) {
            Book newBook = new Book();
            newBook.addBook();
            Library.books.add(newBook);
            System.out.println("Đã thêm sách: " + newBook.title);
        } else if (type == 2) {
            Thesis newThesis = new Thesis();
            newThesis.addThesis();
            Library.theses.add(newThesis);
            System.out.println("Đã thêm luận văn: " + newThesis.title);
        } else {
            System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    // Xóa tài liệu khỏi thư viện theo mã ISBN, ID
    public void removeDocumentByISBN(String isbn, String id) {
        boolean removed = false;

        for (int i = 0; i < Library.books.size(); i++) {
            if (Library.books.get(i).ISBN.equalsIgnoreCase(isbn)) {
                Library.books.remove(i);
                System.out.println("Đã xóa sách có ISBN: " + isbn);
                removed = true;
                break;
            }
        }

        for (int i = 0; i < Library.theses.size(); i++) {
            if (Library.theses.get(i).ID.equalsIgnoreCase(isbn)) {
                Library.theses.remove(i);
                System.out.println("Đã xóa luận văn có ID: " + id);
                removed = true;
                break;
            }
        }

        if (!removed) {
            System.out.println("Không tìm thấy tài liệu để xóa với ISBN: " + isbn);
        }
    }
}

