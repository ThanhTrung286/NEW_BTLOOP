// Tên gói của ứng dụng JavaFX
package com.NEW_BTLOOP.LibraryManagement;

// Import các lớp JavaFX cần thiết
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// Lớp chính của ứng dụng JavaFX, kế thừa từ Application
public class LibraryUIDemo extends Application {

    // Phương thức 'start' là điểm bắt đầu của ứng dụng
    @Override
    public void start(Stage primaryStage) {
        try {
            // Tải file FXML bằng FXMLLoader
            // Sử dụng getClass().getResource() để tìm file FXML
            // Đường dẫn "/com/new_btloop/LibraryUIDemo.fxml" phải khớp với cấu trúc thư mục
            Parent root = FXMLLoader.load(getClass().getResource("/com/NEW_BTLOOP/LibraryManagement/LibraryUIDemo.fxml"));

            // Tạo một Scene mới với giao diện đã tải từ FXML
            Scene scene = new Scene(root);

            // Thiết lập tiêu đề cho cửa sổ
            primaryStage.setTitle("Thư viện - Giao diện quản lý");
            
            // Thiết lập Scene cho Stage chính và hiển thị
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // In stack trace ra console để debug nếu có lỗi khi tải FXML
            e.printStackTrace();
        }
    }

    // Phương thức 'main' là điểm khởi chạy của ứng dụng Java
    public static void main(String[] args) {
        launch(args);
    }
}
