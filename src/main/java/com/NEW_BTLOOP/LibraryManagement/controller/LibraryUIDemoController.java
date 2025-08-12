package com.NEW_BTLOOP.LibraryManagement.controller;

import com.NEW_BTLOOP.LibraryManagement.Book;
import com.NEW_BTLOOP.LibraryManagement.Document;
import com.NEW_BTLOOP.LibraryManagement.Library;
import com.NEW_BTLOOP.LibraryManagement.Thesis;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryUIDemoController {

    // --- Khai báo các thành phần UI từ FXML ---
    @FXML private Button btnAccount;
    @FXML private Button btnSettings;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button btnDashboard;
    @FXML private Button btnDocumentsList;
    @FXML private Button btnLoanManagement;
    @FXML private Button btnReadersManagement;

    // Các View chính
    @FXML private StackPane contentArea;
    @FXML private VBox dashboardView;
    @FXML private FlowPane bookPaneDashboard;

    @FXML private VBox documentsListView;
    @FXML private FlowPane documentCardsPane;
    @FXML private TextField documentFilterField;

    @FXML private VBox documentDetailView;
    @FXML private Label detailBookCoverPlaceholder;
    @FXML private Label detailTitle;
    @FXML private Label detailAuthor;
    @FXML private Label detailCategory;
    @FXML private Label detailDescription;
    @FXML private Button btnBackToDocuments;

    @FXML private VBox loanReturnView;
    @FXML private VBox readersManagementView;

    @FXML private Button btnAddDocument;
    @FXML private Button btnEditDocument;
    @FXML private Button btnDeleteDocument;
    
    // Đối tượng Library để kết nối và thao tác với CSDL
    private Library libraryDB;

    @FXML
public void initialize() {
    System.out.println("Controller đã được khởi tạo và liên kết với FXML!");

    boolean dbReady = false;

    try {
        // Thử kết nối CSDL
        libraryDB = new Library();
        dbReady = true;
        System.out.println("Kết nối CSDL thành công!");
    } catch (SQLException e) {
        System.err.println("❌ Lỗi khi kết nối CSDL: " + e.getMessage());
        // Có thể gán Library giả để UI vẫn chạy
        // libraryDB = new LibraryMock(); // Nếu có class giả lập
    }

    // Nếu DB sẵn sàng mới load dữ liệu
    if (dbReady) {
        populateDashboardBooks();
        showView(dashboardView);
    } else {
        System.err.println("⚠️ Không thể load dữ liệu Dashboard vì DB chưa sẵn sàng.");
        // Hiển thị view dashboard trống hoặc view báo lỗi
        showView(dashboardView);
    }

    // Thiết lập hành động cho các nút
    if (btnDashboard != null) {
        btnDashboard.setOnAction(event -> {
            showView(dashboardView);
            if (libraryDB != null) {
                populateDashboardBooks();
            } else {
                System.err.println("⚠️ Không thể load Dashboard vì libraryDB = null");
            }
        });
    }

    if (btnDocumentsList != null) {
        btnDocumentsList.setOnAction(event -> {
            showView(documentsListView);
            if (libraryDB != null) {
                populateAllDocumentCards();
            } else {
                System.err.println("⚠️ Không thể load danh sách tài liệu vì libraryDB = null");
            }
        });
    }

    if (btnLoanManagement != null) {
        btnLoanManagement.setOnAction(event -> showView(loanReturnView));
    }

    if (btnReadersManagement != null) {
        btnReadersManagement.setOnAction(event -> showView(readersManagementView));
    }

    if (btnBackToDocuments != null) {
        btnBackToDocuments.setOnAction(event -> {
            showView(documentsListView);
            if (libraryDB != null) {
                populateAllDocumentCards();
            }
        });
    }

    if (searchButton != null) {
        searchButton.setOnAction(event -> {
            if (libraryDB != null) {
                String searchText = searchField.getText();
                performSearch(searchText);
            } else {
                System.err.println("⚠️ Không thể tìm kiếm vì libraryDB = null");
            }
        });
    }

    if (btnAddDocument != null) {
        btnAddDocument.setOnAction(event -> handleAddDocument());
    }

    if (btnEditDocument != null) {
        btnEditDocument.setOnAction(event -> handleEditDocument());
    }

    if (btnDeleteDocument != null) {
        btnDeleteDocument.setOnAction(event -> handleDeleteDocument());
    }
}


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

    // --- Các phương thức lấy dữ liệu từ CSDL và hiển thị lên UI ---

    /**
     * Hiển thị các thẻ sách lên Dashboard từ CSDL.
     */
    private void populateDashboardBooks() {
        if (bookPaneDashboard != null) {
            bookPaneDashboard.getChildren().clear();
            try {
                // Lấy danh sách sách từ CSDL
                List<Book> books = libraryDB.listBooks(); 
                // Lấy 6 cuốn sách đầu tiên hoặc tất cả nếu ít hơn 6
                for (int i = 0; i < Math.min(6, books.size()); i++) {
                    Book book = books.get(i);
                    VBox bookCard = createBookCard(book.getTitle(), "Tác giả: " + book.getAuthor());
                    bookCard.setOnMouseClicked(event -> showDocumentDetail(book));
                    bookPaneDashboard.getChildren().add(bookCard);
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi lấy sách cho Dashboard: " + e.getMessage());
                // Xử lý lỗi
            }
        }
    }

    /**
     * Hiển thị tất cả tài liệu (sách và luận văn) vào Documents List View từ CSDL.
     */
    private void populateAllDocumentCards() {
        if (documentCardsPane != null) {
            documentCardsPane.getChildren().clear();
            try {
                // Lấy danh sách sách và luận văn từ CSDL
                List<Book> books = libraryDB.listBooks();
                List<Thesis> theses = libraryDB.listTheses();

                // Thêm các thẻ sách
                for (Book book : books) {
                    VBox documentCard = createBookCard(book.getTitle(), "Tác giả: " + book.getAuthor());
                    documentCard.setOnMouseClicked(event -> showDocumentDetail(book));
                    documentCardsPane.getChildren().add(documentCard);
                }
                
                // Thêm các thẻ luận văn
                for (Thesis thesis : theses) {
                    VBox documentCard = createBookCard(thesis.getTitle(), "Tác giả: " + thesis.getAuthor());
                    documentCard.setOnMouseClicked(event -> showDocumentDetail(thesis));
                    documentCardsPane.getChildren().add(documentCard);
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi lấy danh sách tài liệu: " + e.getMessage());
                // Xử lý lỗi
            }
        }
    }

    /**
     * Phương thức tìm kiếm tài liệu trong CSDL và cập nhật Documents List View.
     */
    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            populateAllDocumentCards(); // Nếu keyword rỗng, hiển thị tất cả
            showView(documentsListView);
            return;
        }

        try {
            Map<String, String> criteria = new HashMap<>();
            criteria.put("title", keyword);
            criteria.put("author", keyword);

            List<Document> foundBooks = libraryDB.searchBookByCriteria(criteria);
            List<Document> foundTheses = libraryDB.searchThesesByCriteria(criteria);
            
            // Xóa các thẻ cũ
            documentCardsPane.getChildren().clear();

            // Thêm kết quả tìm kiếm vào FlowPane
            for (Document doc : foundBooks) {
                VBox card = createBookCard(doc.getTitle(), "Tác giả: " + doc.getAuthor());
                card.setOnMouseClicked(event -> showDocumentDetail(doc));
                documentCardsPane.getChildren().add(card);
            }
            for (Document doc : foundTheses) {
                VBox card = createBookCard(doc.getTitle(), "Tác giả: " + doc.getAuthor());
                card.setOnMouseClicked(event -> showDocumentDetail(doc));
                documentCardsPane.getChildren().add(card);
            }

            showView(documentsListView); // Chuyển sang Documents List View
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm tài liệu: " + e.getMessage());
        }
    }

    // Phương thức tạo thẻ sách (giữ nguyên)
    private VBox createBookCard(String title, String info) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-border-color: #bdc3c7; -fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        card.setPrefWidth(140.0);
        card.setPrefHeight(200.0);

        Label coverPlaceholder = new Label("[Bìa]");
        coverPlaceholder.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-pref-height: 120; -fx-pref-width: 90; -fx-alignment: CENTER; -fx-border-color: #cccccc; -fx-background-color: #f0f0f0;");
        card.getChildren().add(coverPlaceholder);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(120);
        VBox.setMargin(titleLabel, new Insets(5, 0, 0, 0));
        card.getChildren().add(titleLabel);

        if (info != null && !info.isEmpty()) {
            Label infoLabel = new Label(info);
            infoLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
            card.getChildren().add(infoLabel);
        }
        return card;
    }

    private void showDocumentDetail(Document document) {
        System.out.println("Phương thức showDocumentDetail được gọi cho tài liệu: " + document.getTitle());
        if (documentDetailView != null) {
            // Cập nhật thông tin chi tiết vào các Label

            detailBookCoverPlaceholder.setText("Bìa: " + document.getTitle());
            detailTitle.setText("Tên: " + document.getTitle());
            detailAuthor.setText("Tác giả: " + document.getAuthor());

            // Tạo chuỗi mô tả chung ban đầu
            String generalDescription = "ID: " + document.getId() + "\n" +
                                        "Năm xuất bản: " + document.getYear() + "\n" +
                                        "Số lượng có sẵn: " + document.getAvail() + "/" + document.getTotal();

            // Cập nhật thông tin chi tiết riêng cho từng loại tài liệu
            if (document instanceof Book) {
                Book book = (Book) document;
                detailCategory.setText("Thể loại: " + book.getGenre());
                detailDescription.setText(generalDescription + "\n" +
                                         "Nhà xuất bản: " + book.getPublisher() + "\n" +
                                         "Số trang: " + book.getNumberOfPages() + "\n" +
                                         "ISBN: " + book.getISBN());
            } else if (document instanceof Thesis) {
                Thesis thesis = (Thesis) document;
                detailCategory.setText("Loại: Luận văn");
                detailDescription.setText(generalDescription + "\n" +
                                         "Người hướng dẫn: " + thesis.getSupervisor() + "\n" +
                                         "Trường: " + thesis.getUniversity() + "\n" +
                                         "Khoa: " + thesis.getDepartment());
            }
            
            showView(documentDetailView);
        }
    }

    /**
    * Xử lý thêm tài liệu mới.
    */
    private void handleAddDocument() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm Tài Liệu Mới");
        dialog.setHeaderText("Chọn loại tài liệu cần thêm (1: Sách, 2: Luận văn)");
        dialog.setContentText("Loại:");

        dialog.showAndWait().ifPresent(type -> {
            try {
                if ("1".equals(type.trim())) {
                    Book newBook = new Book();
                // Tạm thời, tạo một sách mẫu
                    newBook.setTitle("Sách mới");
                    newBook.setAuthor("Tác giả mới");
                    newBook.setISBN("9999999999999");
                    libraryDB.addBook(newBook);
                
                    System.out.println("Đã thêm sách thành công!");
                    populateAllDocumentCards(); // Cập nhật lại danh sách
                } else if ("2".equals(type.trim())) {
                    Thesis newThesis = new Thesis();
                    // Tạm thời, tạo một luận văn mẫu
                    newThesis.setTitle("Luận văn mới");
                    newThesis.setAuthor("Tác giả luận văn");
                    newThesis.setSupervisor("Người hướng dẫn");
                    newThesis.setDepartment("Khoa Công nghệ thông tin");
                    newThesis.setUniversity("Đại học Bách Khoa");
                    newThesis.setYear(2020);
                    newThesis.setTotal(10);
                    newThesis.setAvail(10);
                    libraryDB.addThesis(newThesis);

                    System.out.println("Đã thêm luận văn thành công!");
                    populateAllDocumentCards(); // Cập nhật lại danh sách
                } else {
                    System.out.println("Lựa chọn không hợp lệ.");
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi thêm tài liệu: " + e.getMessage());
                // Hiển thị alert báo lỗi
                showAlert("Lỗi", "Thêm tài liệu thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    

    /**
    * Xử lý sửa tài liệu.
    */
    

    private void handleEditDocument() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Sửa Tài Liệu");
        idDialog.setHeaderText("Nhập ID của tài liệu cần sửa:");
        idDialog.setContentText("ID:");

        idDialog.showAndWait().ifPresent(id -> {
            try {
                if (id.substring(0, 3).equals("BOK")) {
                    Book book = libraryDB.getBookByID(id);
                    if (book != null) {
                        // Mở một Dialog mới để sửa thông tin sách
                        showEditBookDialog(book);
                    } else {
                        showAlert("Lỗi", "Không tìm thấy sách với ID: " + id, Alert.AlertType.ERROR);
                    }
                }  else {
                    showAlert("Lỗi", "ID tài liệu không hợp lệ.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Lỗi CSDL khi tìm tài liệu: " + e.getMessage(), Alert.AlertType.ERROR);
            } catch (StringIndexOutOfBoundsException e) {
                showAlert("Lỗi", "ID không đúng định dạng.", Alert.AlertType.ERROR);
            }
        });
    }

     // Thêm phương thức mới để tạo và hiển thị Dialog sửa thông tin sách
    private void showEditBookDialog(Book book) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Sửa thông tin sách");
        dialog.setHeaderText("Chỉnh sửa thông tin cho sách có ID: " + book.getId());

        // Tạo các trường nhập liệu
        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField publisherField = new TextField(book.getPublisher());
        TextField GenreField = new TextField(book.getGenre());

        VBox content = new VBox(10, new Label("Tên sách:"), titleField,
                                 new Label("Tác giả:"), authorField,
                                 new Label("Nhà xuất bản:"), publisherField,
                                 new Label("Thể loại:"), GenreField);
        dialog.getDialogPane().setContent(content);

        // Thêm các nút OK và Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
        // Xử lý khi nhấn OK
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setPublisher(publisherField.getText());
                book.setGenre(GenreField.getText());

                // Cập nhật các trường còn lại
                return book;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedBook -> {
            try {
                libraryDB.updateBook(updatedBook);
                showAlert("Thành công", "Đã cập nhật thông tin sách thành công!", Alert.AlertType.INFORMATION);
                populateAllDocumentCards(); // Cập nhật lại danh sách
            } catch (SQLException e) {
                showAlert("Lỗi", "Cập nhật sách thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }


    /**
    * Xử lý xóa tài liệu.
    */
    private void handleDeleteDocument() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Xóa Tài Liệu");
        dialog.setHeaderText("Nhập ID của tài liệu cần xóa:");
        dialog.setContentText("ID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                libraryDB.deleteDoc(id);
                System.out.println("Đã xóa tài liệu với ID: " + id);
                populateAllDocumentCards(); // Cập nhật lại danh sách
                showAlert("Thông báo", "Xóa tài liệu thành công!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                System.err.println("Lỗi khi xóa tài liệu: " + e.getMessage());
                showAlert("Lỗi", "Xóa tài liệu thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}