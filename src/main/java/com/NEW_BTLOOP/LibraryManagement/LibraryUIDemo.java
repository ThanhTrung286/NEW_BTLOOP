
package com.NEW_BTLOOP.LibraryManagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryUIDemo extends Application {

    // Phương thức 'start' là điểm bắt đầu của ứng dụng
    @Override
    public void start(Stage primaryStage) {
        try {         
            Parent root = FXMLLoader.load(getClass().getResource("/com/NEW_BTLOOP/LibraryManagement/LibraryUIDemo.fxml"));

            // Tạo một Scene mới với giao diện đã tải từ FXML
            Scene scene = new Scene(root);

            primaryStage.setTitle("Thư viện - Giao diện quản lý");
            
            // Thiết lập Scene cho Stage chính và hiển thị
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            // In stack trace ra console để debug nếu có lỗi khi tải FXML
            e.printStackTrace();
        }
    }

    
}
