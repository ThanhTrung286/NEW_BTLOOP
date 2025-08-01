package com.NEW_BTLOOP.LibraryManagement;


import java.util.ArrayList;
import java.util.Scanner;

public class Library {
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Book> books = new ArrayList<>();
    public static ArrayList<Thesis> theses = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    // Chức năng 1: Thêm tài liệu
    public void addDocument() {
        System.out.println("Thêm loại tài liệu nào?");
        System.out.println("1. Sách");
        System.out.println("2. Luận văn");
        int type = scanner.nextInt();
        scanner.nextLine();

        if (type == 1) {
            Book book = new Book();
            book.addBook();
            books.add(book);
            System.out.println("Đã thêm sách thành công.");
        } else if (type == 2) {
            Thesis thesis = new Thesis();
            thesis.addThesis();
            theses.add(thesis);
            System.out.println("Đã thêm luận văn thành công.");
        } else {
            System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    // Chức năng 2: Xóa tài liệu
    public void removeDocument() {
        System.out.println("Nhập mã ID tài liệu cần xóa:");
        String id = scanner.nextLine();

        if (books.removeIf(book -> book.ID.equalsIgnoreCase(id)) ||
                theses.removeIf(thesis -> thesis.ID.equalsIgnoreCase(id))) {
            System.out.println("Đã xóa tài liệu có mã ID: " + id);
        } else {
            System.out.println("Không tìm thấy tài liệu.");
        }
    }

    // Chức năng 3: Cập nhật tài liệu
    public void updateDocument() {
        System.out.println("Nhập mã ID tài liệu cần cập nhật:");
        String id = scanner.nextLine();

        for (Book book : books) {
            if (book.ID.equalsIgnoreCase(id)) {
                book.updateBook();
                return;
            }
        }

        for (Thesis thesis : theses) {
            if (thesis.ID.equalsIgnoreCase(id)) {
                thesis.updateThesis();
                return;
            }
        }

        System.out.println("Không tìm thấy tài liệu.");
    }

    // Chức năng 4: Tìm kiếm sách hoặc luận án
    public void searchMenu() {
        while (true) {
            System.out.println("\n--- MENU TÌM KIẾM ---");
            System.out.println("1. Tìm trong SÁCH");
            System.out.println("2. Tìm trong LUẬN VĂN");
            System.out.println("0. Quay lại");
            System.out.print("Chọn loại tài liệu: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    searchBookFields();
                    break;
                case 2:
                    searchThesisFields();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ.");
            }
        }
    }
    // Tìm sách
    private void searchBookFields() {
        System.out.println("\nTìm kiếm SÁCH theo:");
        System.out.println("1. Tiêu đề");
        System.out.println("2. Tác giả");
        System.out.println("3. Mã ID");
        System.out.println("4. Mã ISBN");
        System.out.print("Chọn tiêu chí: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nhập từ khóa: ");
        String keyword = scanner.nextLine();

        switch (option) {
            case 1 -> Book.searchBooksByTitle(keyword);
            case 2 -> Book.searchBooksByAuthor(keyword);
            case 3 -> Book.searchBooksByID(keyword);
            case 4 -> Book.searchBooksByISBN(keyword);
            default -> System.out.println("Tiêu chí không hợp lệ.");
        }
    }

    private void searchThesisFields() {
        System.out.println("\nTìm kiếm LUẬN VĂN theo:");
        System.out.println("1. Tiêu đề");
        System.out.println("2. Tác giả");
        System.out.println("3. Mã ID");
        System.out.println("4. Người hướng dẫn");
        System.out.print("Chọn tiêu chí: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nhập từ khóa: ");
        String keyword = scanner.nextLine();

        switch (option) {
            case 1 -> Thesis.searchThesesByTitle(keyword);
            case 2 -> Thesis.searchThesesByAuthor(keyword);
            case 3 -> Thesis.searchThesesByID(keyword);
            case 4 -> Thesis.searchThesesBySupervisor(keyword);
            default -> System.out.println("Tiêu chí không hợp lệ.");
        }
    }

    // Chức năng 5: Hiển thị tất cả tài liệu
    public void displayDocuments() {
        System.out.println("--- DANH SÁCH SÁCH ---");
        for (Book book : books) {
            System.out.println(book.getInfo());
        }

        System.out.println("--- DANH SÁCH LUẬN VĂN ---");
        for (Thesis thesis : theses) {
            System.out.println(thesis.getInfo());
        }
    }

    // Chức năng 7: Mượn tài liệu
    public void borrowDocument() {
        System.out.println("Nhập mã ID tài liệu muốn mượn:");
        String id = scanner.nextLine();

        for (Book book : books) {
            if (book.ID.equalsIgnoreCase(id)) {
                book.borrowDocument();
                return;
            }
        }

        for (Thesis thesis : theses) {
            if (thesis.ID.equalsIgnoreCase(id)) {
                thesis.borrowDocument();
                return;
            }
        }

        System.out.println("Không tìm thấy tài liệu.");
    }

    // Chức năng 8: Trả tài liệu
    public void returnDocument() {
        System.out.println("Nhập mã ID tài liệu muốn trả:");
        String id = scanner.nextLine();

        for (Book book : books) {
            if (book.ID.equalsIgnoreCase(id)) {
                book.returnDocument();
                return;
            }
        }

        for (Thesis thesis : theses) {
            if (thesis.ID.equalsIgnoreCase(id)) {
                thesis.returnDocument();
                return;
            }
        }

        System.out.println("Không tìm thấy tài liệu.");
    }
    
     // Chức năng 9: Thêm người dùng
    public void addUser() {
        User user = new User();
        users.add(user);
        System.out.println("Đã thêm người dùng.");
    }

    // Chức năng 10: Hiển thị người dùng
    public void displayUser() {
        System.out.println("--- DANH SÁCH NGƯỜI DÙNG ---");
        for (User user : users) {
            System.out.println(user.getInfo());
        }
    }
}
