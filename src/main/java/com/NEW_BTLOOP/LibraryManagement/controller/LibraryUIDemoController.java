package com.NEW_BTLOOP.LibraryManagement.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node; // Import Node cho phương thức showView
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator; // Thêm import Separator nếu giữ lại phần trang trí
import javafx.scene.control.Hyperlink; // Thêm import Hyperlink nếu giữ lại phần trang trí
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region; // Import Region
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font; // Thêm Font nếu tùy chỉnh font trong code

public class LibraryUIDemoController {

    // --- Khai báo các thành phần UI từ FXML ---
    // Các nút trên Header và Left Menu
    @FXML private Button btnAccount;
    @FXML private Button btnSettings;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button btnDashboard;
    @FXML private Button btnDocumentsList;
    @FXML private Button btnLoanManagement;
    @FXML private Button btnReadersManagement;

    // Các View chính trong StackPane
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private FlowPane bookPaneDashboard; // FlowPane trong Dashboard

    @FXML private VBox documentsListView; // View Danh sách tài liệu
    @FXML private FlowPane documentCardsPane; // FlowPane mới để chứa các thẻ tài liệu
    @FXML private TextField documentFilterField; // Để thao tác với danh sách 

    @FXML private VBox documentDetailView; // View Thông tin chi tiết tài liệu
    @FXML private Label detailBookCoverPlaceholder;
    @FXML private Label detailTitle;
    @FXML private Label detailAuthor;
    @FXML private Label detailCategory;
    @FXML private Label detailDescription;
    @FXML private Button btnBackToDocuments;

    @FXML private VBox loanReturnView;
    @FXML private VBox readersManagementView;


    @FXML
    public void initialize() {
        System.out.println("Controller đã được khởi tạo và liên kết với FXML!");

        // 1. Mặc định hiển thị Dashboard khi ứng dụng khởi chạy
        showView(dashboardView);
        populateDashboardBooks(); // Tạo các thẻ sách cho Dashboard

        // 2. Thiết lập hành động cho các nút trên Menu bên trái
        if (btnDashboard != null) {
            btnDashboard.setOnAction(event -> showView(dashboardView));
        }
        if (btnDocumentsList != null) {
            btnDocumentsList.setOnAction(event -> {
                showView(documentsListView);
                populateDocumentCards(); // Khi chuyển sang Danh sách tài liệu, tạo các thẻ
            });
        }
        if (btnLoanManagement != null) {
            btnLoanManagement.setOnAction(event -> showView(loanReturnView));
        }
        if (btnReadersManagement != null) {
            btnReadersManagement.setOnAction(event -> showView(readersManagementView));
        }

        // 3. Thiết lập hành động cho nút "Quay lại" trong Document Detail View
        if (btnBackToDocuments != null) {
            btnBackToDocuments.setOnAction(event -> showView(documentsListView));
        }

        // 4. Thiết lập hành động cho nút tìm kiếm chung
        if (searchButton != null) {
            searchButton.setOnAction(event -> {
                String searchText = searchField.getText();
                System.out.println("Tìm kiếm chung: " + searchText);
                // Triển khai logic tìm kiếm ở đây nếu cần
            });
        }

        // Các nút thêm/sửa/xóa trong documentsListView (hiện tại chưa có fx:id, nhưng nếu bạn thêm sẽ cần xử lý)
        // Ví dụ: fx:id="btnAddDocument"
        // if (btnAddDocument != null) {
        //     btnAddDocument.setOnAction(event -> System.out.println("Thêm tài liệu"));
        // }
    }

    /**
     * Phương thức để hiển thị một View cụ thể và ẩn tất cả các View khác trong StackPane.
     * @param viewToShow Node (thường là VBox hoặc Pane khác) cần được hiển thị.
     */
    private void showView(Node viewToShow) {
        if (contentArea != null && contentArea.getChildren() != null) {
            for (Node node : contentArea.getChildren()) {
                node.setVisible(false);
                node.setManaged(false);
            }
            if (viewToShow != null) {
                viewToShow.setVisible(true);
                viewToShow.setManaged(true);
            }
        }
    }

    /**
     * Phương thức giả lập để thêm các thẻ sách vào Dashboard.
     * Khi click vào thẻ sách sẽ hiển thị giao diện chi tiết.
     */
    private void populateDashboardBooks() {
        if (bookPaneDashboard != null) {
            bookPaneDashboard.getChildren().clear();
            for (int i = 1; i <= 6; i++) { // Giả lập 6 thẻ sách phổ biến
                VBox bookCard = createBookCard("Sách " + i, (100 + i * 5) + ""); // Tạo thẻ sách
                // Thêm sự kiện click vào mỗi thẻ sách trên Dashboard
                final int bookId = i;
                bookCard.setOnMouseClicked(event -> showBookDetail(bookId));
                bookPaneDashboard.getChildren().add(bookCard);
            }
        }
    }

    /**
     * Phương thức giả lập để thêm các thẻ tài liệu vào Documents List View.
     * Khi click vào thẻ tài liệu sẽ hiển thị giao diện chi tiết.
     */
    private void populateDocumentCards() {
        if (documentCardsPane != null) {
            documentCardsPane.getChildren().clear(); // Xóa các thẻ cũ nếu có
            for (int i = 1; i <= 20; i++) { // Giả lập 20 tài liệu
                VBox documentCard = createBookCard("Tài liệu " + i, null); // Không hiển thị lượt đọc cho danh sách chung
                final int docId = i;
                documentCard.setOnMouseClicked(event -> showBookDetail(docId)); // Sử dụng lại showBookDetail
                documentCardsPane.getChildren().add(documentCard);
            }
        }
    }

    /**
     * Phương thức chung để tạo một thẻ sách/tài liệu.
     * @param title Tên của sách/tài liệu.
     * @param info Phụ đề (ví dụ: "Lượt đọc: XXX" hoặc null).
     * @return VBox đại diện cho một thẻ sách/tài liệu.
     */
    private VBox createBookCard(String title, String info) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #bdc3c7; -fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        card.setPrefWidth(140.0);
        card.setPrefHeight(200.0); // Tăng chiều cao để có không gian cho bìa và tên

        // Placeholder cho bìa sách
        Label coverPlaceholder = new Label("[Bìa]");
        coverPlaceholder.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-pref-height: 120; -fx-pref-width: 90; -fx-alignment: CENTER; -fx-border-color: #cccccc; -fx-background-color: #f0f0f0;");
        card.getChildren().add(coverPlaceholder);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-alignment: center;");
        titleLabel.setWrapText(true); // Cho phép xuống dòng nếu tên dài
        titleLabel.setMaxWidth(120); // Giới hạn chiều rộng
        VBox.setMargin(titleLabel, new Insets(5, 0, 0, 0));
        card.getChildren().add(titleLabel);

        if (info != null && !info.isEmpty()) {
            Label infoLabel = new Label(info);
            infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
            card.getChildren().add(infoLabel);
        }
        return card;
    }


    /**
     * Phương thức giả lập hiển thị chi tiết sách/tài liệu khi một thẻ được click.
     * @param docId ID của tài liệu được chọn (giả lập).
     */
    private void showBookDetail(int docId) {
        if (documentDetailView != null) {
            // Cập nhật thông tin chi tiết giả lập vào các Label trong documentDetailView
            detailBookCoverPlaceholder.setText("Bìa: Tài liệu ID " + docId);
            detailTitle.setText("Tên: Tài liệu Chi Tiết " + docId);
            detailAuthor.setText("Tác giả: Tác Giả Giả Lập " + docId);
            detailCategory.setText("Thể loại: Khoa học");
            detailDescription.setText("Mô tả: Đây là mô tả chi tiết giả lập cho tài liệu số " + docId + ". Nội dung này có thể dài hơn và sẽ tự động xuống dòng.");

            showView(documentDetailView); // Hiển thị View chi tiết tài liệu
        }
    }
}