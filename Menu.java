package NEW_BTLOOP;

import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        DocManager docManager = new DocManager(); // Tạo đối tượng quản lý tài liệu

        while (true) {
            System.out.println("Welcome to My Application!");
            System.out.println("[0] Exit");
            System.out.println("[1] Add Document");
            System.out.println("[2] Remove Document");
            System.out.println("[3] Update Document");
            System.out.println("[4] Find Document");
            System.out.println("[5] Display Document");
            System.out.println("[6] Add User");
            System.out.println("[7] Borrow Document");
            System.out.println("[8] Return Document");
            System.out.println("[9] Display User Info");
            System.out.print("Please choose an option: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 0:
                        System.out.println("Exiting application. Goodbye!");
                        return;
                    case 1: //Thêm tài liệu
                        System.out.println(">> Add Document selected");
                        
                        break;
                    case 2:
                        System.out.println(">> Remove Document selected");
                        break;
                    case 3:
                        System.out.println(">> Update Document selected");
                        break;
                    case 4:
                        System.out.println(">> Find Document selected");
                        break;
                    case 5:
                        System.out.println(">> Display Document selected");
                        break;
                    case 6:
                        System.out.println(">> Add User selected");
                        break;
                    case 7:
                        System.out.println(">> Borrow Document selected");
                        break;
                    case 8:
                        System.out.println(">> Return Document selected");
                        break;
                    case 9:
                        System.out.println(">> Display User Info selected");
                        break;
                    default:
                        System.out.println("Action is not supported");
                }
            } else {
                System.out.println("Action is not supported");
                scanner.nextLine(); // Xóa nhập sai 
            }
        }
    }
}

