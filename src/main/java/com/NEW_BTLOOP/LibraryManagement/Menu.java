package com.NEW_BTLOOP.LibraryManagement;


import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        Library library = new Library(); // Tạo đối tượng quản lý tài liệu

        while (true) {
            System.out.println("QUẢN LÝ THƯ VIỆN!");
            System.out.println("[0] Thoát");
            System.out.println("[1] Thêm tài liệu");
            System.out.println("[2] Xoá tài liệu");
            System.out.println("[3] Sửa thông tin tài liệu");
            System.out.println("[4] Tìm tài liệu");
            System.out.println("[5] Xem danh sách tài liệu");
            System.out.println("[6] Thêm người dùng");
            System.out.println("[7] Mượn tài liệu");
            System.out.println("[8] Trả tài liệu");
            System.out.println("[9] Xem danh sách người dùng");
            System.out.print("Chọn chức năng: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 0:
                        System.out.println(">> Chương trình đã thoát");
                        System.exit(0);
                    case 1:
                        System.out.println(">> Thêm tài liệu");
                        library.addDocument();
                        break;
                    case 2:
                        System.out.println(">> Xoá tài liệu");
                        library.removeDocument();
                        break;
                    case 3:
                        System.out.println(">> Sửa thông tin tài liệu");
                        library.updateDocument();
                        break;
                    case 4:
                        System.out.println(">> Tìm tài liệu");
                        library.searchMenu();
                        break;
                    case 5:
                        System.out.println(">> Xem danh sách tài liệu");
                        library.displayDocuments();
                        break;
                    case 6:
                        System.out.println(">> Thêm người dùng");
                        library.addUser();
                        break;
                    case 7:
                        System.out.println(">> Mượn tài liệu");
                        library.borrowDocument();
                        break;
                    case 8:
                        System.out.println(">> Trả tài liệu");
                        library.returnDocument();
                        break;
                    case 9:
                        System.out.println(">> Xem danh sách người dùng");
                        library.displayUser();
                        break;
                }
            } else {
                System.out.println("Lựa chọn không hợp lệ.");
                scanner.nextLine(); // Xóa nhập sai
            }
        }
    }
}
