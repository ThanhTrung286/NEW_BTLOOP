package com.NEW_BTLOOP.LibraryManagement.controller;

import com.NEW_BTLOOP.LibraryManagement.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LibraryUIDemoController {

    // --- Khai báo các thành phần UI từ FXML ---
    @FXML
    private Button btnAccount;
    @FXML
    private Button btnSettings;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ToggleGroup menuGroup;
    @FXML
    private ToggleButton btnDashboard;
    @FXML
    private ToggleButton btnDocumentsList;
    @FXML
    private ToggleButton btnLoanManagement;
    @FXML
    private ToggleButton btnReadersManagement;

    // Các View chính
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox dashboardView;
    @FXML
    private FlowPane bookPaneDashboard;

    @FXML
    private VBox documentsListView;
    @FXML
    private FlowPane documentCardsPane;
    @FXML
    private TextField documentFilterField;

    @FXML
    private VBox documentDetailView;
    @FXML
    private Label detailBookCoverPlaceholder;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailAuthor;
    @FXML
    private Label detailCategory;
    @FXML
    private Label detailDescription;
    @FXML
    private Button btnBackToDocuments;

    @FXML
    private VBox loanReturnView;
    @FXML
    private VBox readersManagementView;

    @FXML
    private Button btnAddDocument;
    @FXML
    private Button btnEditDocument;
    @FXML
    private Button btnDeleteDocument;

    @FXML
    private TableView<User> readersTable;
    @FXML
    private TableColumn<User, String> colUserID;
    @FXML
    private TableColumn<User, String> colName;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, Integer> colBorrowedDocs;
    @FXML
    private TableColumn<User, Integer> colBorrowedLimit;
    @FXML
    private Pagination readersPagination;
    @FXML
    private Button btnAddReader;
    @FXML
    private Button btnEditReader;
    @FXML
    private Button btnDeleteReader;

    @FXML
    private TableView<BorrowRecord> borrowTable;

    @FXML
    private TableColumn<BorrowRecord, String> recordIdCol;
    @FXML
    private TableColumn<BorrowRecord, String> userIdCol;
    @FXML
    private TableColumn<BorrowRecord, String> documentIdCol;
    @FXML
    private TableColumn<BorrowRecord, LocalDate> borrowDateCol;
    @FXML
    private TableColumn<BorrowRecord, LocalDate> expectedReturnDateCol;
    @FXML
    private TableColumn<BorrowRecord, LocalDate> actualReturnDateCol;
    @FXML
    private Button btnBorrow;
    @FXML
    private Button btnReturn;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnLogout;

    // Biến trạng thái đăng nhập
    private boolean isLoggedIn = false;

    // Đối tượng Library để kết nối và thao tác với CSDL
    private Library libraryDB;
    private UserMan userMan;
    private ObservableList<User> allReaders = FXCollections.observableArrayList();
    private ObservableList<BorrowRecord> allRecords = FXCollections.observableArrayList();
    private static final int ITEMS_PER_PAGE = 20;

    @FXML
    public void initialize() {
        System.out.println("Controller đã được khởi tạo và liên kết với FXML!");

        try {
            libraryDB = new Library();
            userMan = new UserMan();
            System.out.println("Kết nối CSDL thành công!");
            populateDashboardBooks();
            showView(dashboardView);
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi kết nối CSDL: " + e.getMessage());
            showView(dashboardView);
        }
        ToggleGroup menuGroup = new ToggleGroup();
        // Ensure Dashboard is selected by default
        btnDashboard.setSelected(true);
        btnDashboard.setToggleGroup(menuGroup);
        btnDocumentsList.setToggleGroup(menuGroup);
        btnLoanManagement.setToggleGroup(menuGroup);
        btnReadersManagement.setToggleGroup(menuGroup);
        btnDashboard.setOnAction(event -> {
        handleNavigation(btnDashboard, dashboardView, () -> {});
    });
    btnDocumentsList.setOnAction(event -> {
        handleNavigation(btnDocumentsList, documentsListView, () -> populateAllDocumentCards());
    });
    btnLoanManagement.setOnAction(event -> {
        handleNavigation(btnLoanManagement, loanReturnView, () -> {});
    });
    btnReadersManagement.setOnAction(event -> {
        handleNavigation(btnReadersManagement, readersManagementView, () -> populateReadersList());
    });

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
                }
            });
        }

        if (btnAddDocument != null)
            btnAddDocument.setOnAction(e -> handleAddDocument());
        if (btnEditDocument != null)
            btnEditDocument.setOnAction(e -> handleEditDocument());
        if (btnDeleteDocument != null)
            btnDeleteDocument.setOnAction(e -> handleDeleteDocument());
        // Gán sự kiện cho các nút quản lý người đọc
        btnReadersManagement.setOnAction(event -> {
            showView(readersManagementView);
            populateReadersList(); // Tải dữ liệu khi chuyển sang tab Quản lý người đọc
        });
        btnAddReader.setOnAction(event -> handleAddReader());
        btnEditReader.setOnAction(event -> handleEditReader());
        btnDeleteReader.setOnAction(event -> handleDeleteReader());

        btnBorrow.setOnAction(event -> handleBorrowDocument());
        btnReturn.setOnAction(event -> handleReturnDocument());
        // Cấu hình các cột của TableView
        colUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colBorrowedDocs.setCellValueFactory(new PropertyValueFactory<>("borrowedDocuments"));
        colBorrowedLimit.setCellValueFactory(new PropertyValueFactory<>("borrowedLimit"));

        recordIdCol.setCellValueFactory(new PropertyValueFactory<>("recordID"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        documentIdCol.setCellValueFactory(new PropertyValueFactory<>("documentID"));
        borrowDateCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        expectedReturnDateCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        actualReturnDateCol.setCellValueFactory(new PropertyValueFactory<>("actualReturnDate"));

        // Tải dữ liệu và phân trang
        loadAllReaders();
        setupPagination();

        loadAllRecords();
        // Khởi tạo các view ban đầu
        populateDashboardBooks();
        populateAllDocumentCards();
        populateReadersList();
        populateRecordList();

        System.out.println("Số lượng readers: " + allReaders.size());
        for (User u : allReaders) {
            System.out.println(u.getUserID() + " - " + u.getName());
        }

        readersTable.setItems(allReaders);
        borrowTable.setItems(allRecords);
        if (btnBackToDocuments != null) {
            btnBackToDocuments.setOnAction(event -> {
                showView(documentsListView);
                if (libraryDB != null) {
                    populateAllDocumentCards();
                }
            });
        }
        btnLogin.setOnAction(event -> handleLogin());
        btnLogout.setOnAction(event -> handleLogout());
        updateLoginStatus(); // Đảm bảo trạng thái ban đầu là "chưa đăng nhập"
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
            List<Document> foundDoc = libraryDB.generalSearch(keyword);

            // Xóa các thẻ cũ
            documentCardsPane.getChildren().clear();

            // Thêm kết quả tìm kiếm vào FlowPane
            for (Document doc : foundDoc) {
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
        card.setStyle(
                "-fx-border-color: #bdc3c7; -fx-background-color: #ffffff; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
        card.setPrefWidth(140.0);
        card.setPrefHeight(200.0);

        Label coverPlaceholder = new Label("[Bìa]");
        coverPlaceholder.setStyle(
                "-fx-font-weight: bold; -fx-font-size: 14px; -fx-pref-height: 120; -fx-pref-width: 90; -fx-alignment: CENTER; -fx-border-color: #cccccc; -fx-background-color: #f0f0f0;");
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
                    Document doc = libraryDB.getDocumentById(id);
                    if (doc != null) {
                        // Mở một Dialog mới để sửa thông tin sách
                        if (doc instanceof Book) {
                            Book book = (Book) doc;
                            showEditBookDialog(book);
                        }

                    } else {
                        showAlert("Lỗi", "Không tìm thấy sách với ID: " + id, Alert.AlertType.ERROR);
                    }
                } else {
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

    // -------------------- CÁC CHỨC NĂNG QUẢN LÝ NGƯỜI ĐỌC --------------------
    private void loadAllReaders() {
        try {
            allReaders.clear();
            allReaders.addAll(userMan.listUsers());
        } catch (SQLException e) {
            showAlert("Lỗi CSDL", "Không thể tải danh sách người dùng: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupPagination() {
        int pageCount = (int) Math.ceil((double) allReaders.size() / ITEMS_PER_PAGE);
        readersPagination.setPageCount(pageCount > 0 ? pageCount : 1);

        readersPagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, allReaders.size());

            // chỉ CẬP NHẬT dữ liệu cho bảng
            readersTable.setItems(FXCollections.observableArrayList(
                    allReaders.subList(fromIndex, toIndex)));

            // KHÔNG trả về readersTable nữa
            return new Pane(); // hoặc: return null;
        });

        // Hiển thị luôn trang đầu tiên
        if (!allReaders.isEmpty()) {
            readersPagination.setCurrentPageIndex(0);
            readersTable.setItems(FXCollections.observableArrayList(
                    allReaders.subList(0, Math.min(ITEMS_PER_PAGE, allReaders.size()))));
        } else {
            readersTable.setItems(FXCollections.observableArrayList());
        }
    }

    // Chức năng thêm người đọc
    private void handleAddReader() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Thêm Người Đọc Mới");
        dialog.setHeaderText("Nhập thông tin chi tiết của người đọc:");

        TextField nameField = new TextField();
        nameField.setPromptText("Họ và Tên");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField borrowedLimitField = new TextField();
        borrowedLimitField.setPromptText("Hạn mức mượn");

        VBox content = new VBox(10,
                new Label("Họ và Tên:"), nameField,
                new Label("Email:"), emailField,
                new Label("Hạn mức mượn:"), borrowedLimitField);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                User newUser = new User();
                newUser.setName(nameField.getText());
                newUser.setEmail(emailField.getText());
                newUser.setBorrowedLimit(Integer.parseInt(borrowedLimitField.getText()));
                newUser.setBorrowedDocuments(0);
                return newUser;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newUser -> {
            try {
                userMan.insertUser(newUser);
                showAlert("Thành công", "Đã thêm người đọc thành công!", Alert.AlertType.INFORMATION);
                populateReadersList();
            } catch (SQLException e) {
                showAlert("Lỗi", "Thêm người đọc thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Chức năng sửa người đọc
    private void handleEditReader() {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Sửa Thông Tin Người Đọc");
        idDialog.setHeaderText("Nhập UserID của người đọc cần sửa:");
        idDialog.setContentText("UserID:");

        idDialog.showAndWait().ifPresent(id -> {
            try {
                User user = userMan.getUserByID(id);
                if (user != null) {
                    showEditUserDialog(user);
                } else {
                    showAlert("Lỗi", "Không tìm thấy người dùng với UserID: " + id, Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Lỗi", "Lỗi CSDL khi tìm người dùng: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showEditUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Sửa Thông Tin Người Đọc");
        dialog.setHeaderText("Chỉnh sửa thông tin cho người đọc có UserID: " + user.getUserID());

        TextField nameField = new TextField(user.getName());
        TextField emailField = new TextField(user.getEmail());
        TextField borrowedLimitField = new TextField(String.valueOf(user.getBorrowedLimit()));

        VBox content = new VBox(10,
                new Label("Họ và Tên:"), nameField,
                new Label("Email:"), emailField,
                new Label("Hạn mức mượn:"), borrowedLimitField);
        dialog.getDialogPane().setContent(content);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                user.setName(nameField.getText());
                user.setEmail(emailField.getText());
                user.setBorrowedLimit(Integer.parseInt(borrowedLimitField.getText()));
                return user;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedUser -> {
            try {
                userMan.updateUser(updatedUser);
                showAlert("Thành công", "Đã cập nhật thông tin người dùng thành công!", Alert.AlertType.INFORMATION);
                populateReadersList();
            } catch (SQLException e) {
                showAlert("Lỗi", "Cập nhật người dùng thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    // Chức năng xóa người đọc
    private void handleDeleteReader() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Xóa Người Đọc");
        dialog.setHeaderText("Nhập UserID của người đọc cần xóa:");
        dialog.setContentText("UserID:");

        dialog.showAndWait().ifPresent(id -> {
            try {
                userMan.deleteUser(id);
                showAlert("Thành công", "Đã xóa người dùng thành công!", Alert.AlertType.INFORMATION);
                populateReadersList();
            } catch (SQLException e) {
                showAlert("Lỗi", "Xóa người dùng thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    //-------------------- CÁC CHỨC NĂNG QUẢN LÝ ĐĂNG NHẬP --------------------

    private void handleNavigation(ToggleButton btnDashboard2, VBox view, Runnable task) {
        if (isLoggedIn) {
            showView(view);
            task.run(); // Chạy tác vụ tải dữ liệu
        } else {
            showAlert("Thông báo", "Vui lòng đăng nhập để truy cập chức năng này.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void handleLogin() {
        // Tạo hộp thoại đăng nhập
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Đăng nhập");

        // Tạo các trường nhập liệu
        VBox content = new VBox(10);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên người dùng");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mật khẩu");

        content.getChildren().addAll(new Label("Tên người dùng:"), usernameField,
                                 new Label("Mật khẩu:"), passwordField);
        dialog.getDialogPane().setContent(content);

        // Thêm các nút OK và Cancel
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Chuyển đổi kết quả
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Pair<>(usernameField.getText(), passwordField.getText());
            }
            return null;
        });

    // Xử lý kết quả khi đăng nhập
    Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(credentials -> {
            String username = credentials.getKey();
            String password = credentials.getValue();
        
            // Kiểm tra thông tin đăng nhập
            if (username.equals("root") && password.equals("root")) {
               showAlert("Thông báo", "Đăng nhập thành công!", Alert.AlertType.INFORMATION);
               isLoggedIn = true;
               updateLoginStatus();
            } else {
               showAlert("Lỗi", "Tên người dùng hoặc mật khẩu không đúng.", Alert.AlertType.ERROR);
            }
        });
    }
    private void updateLoginStatus() {
    btnLogin.setVisible(!isLoggedIn);
    btnLogin.setManaged(!isLoggedIn);
    
    btnLogout.setVisible(isLoggedIn);
    btnLogout.setManaged(isLoggedIn);
    }

    @FXML
    private void handleLogout() {
        isLoggedIn = false;
        updateLoginStatus();
        showAlert("Thông báo", "Đã đăng xuất.", Alert.AlertType.INFORMATION);
    }
    // -------------------- CÁC CHỨC NĂNG HỖ TRỢ --------------------

    private void populateReadersList() {
        loadAllReaders();
        setupPagination();
    }

    private void populateRecordList() {
        loadAllRecords();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // quản lí mượn trả
    private void loadAllRecords() {
        try {
            allRecords.clear();
            allRecords.addAll(userMan.listBorrowRecords());
        } catch (SQLException e) {
            showAlert("Lỗi CSDL", "Không thể tải lịch sử mượn trả: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Chức năng cho mượn
    private void handleBorrowDocument() {
        Dialog dialog = new TextInputDialog();
        dialog.setTitle("Mượn Tài Liệu");
        dialog.setHeaderText("Nhập UserID và DocumentID để mượn tài liệu:");

        // dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,
        // ButtonType.CANCEL);

        VBox content = new VBox(10);
        TextField userIdField = new TextField();
        userIdField.setPromptText("UserID");
        TextField documentIdField = new TextField();
        documentIdField.setPromptText("DocumentID");
        TextField daysField = new TextField();
        daysField.setPromptText("Số ngày mượn");

        content.getChildren().addAll(new Label("UserID:"), userIdField, new Label("DocumentID:"), documentIdField,
                new Label("Số ngày mượn:"), daysField);
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                BorrowRecord brw = new BorrowRecord();
                brw.setUserID(userIdField.getText());
                brw.setDocumentID(documentIdField.getText());
                brw.setBorrowDate(LocalDate.now());
                brw.setExpectedReturnDate(brw.getBorrowDate().plusDays(Integer.parseInt(daysField.getText())));
                // Cập nhật các trường còn lại
                return brw;
            }
            return null;
        }
        );
        dialog.showAndWait().ifPresent(result -> {
            String userId = userIdField.getText();
            String documentId = documentIdField.getText();
            int days = Integer.parseInt(daysField.getText());
            try {
                userMan.insertBorrowRecord(userId, documentId, days);
                populateRecordList();
                showAlert("Thành công", "Đã mượn tài liệu thành công!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Lỗi", "Mượn tài liệu thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
    }

    // Chức năng trả tài liệu
    private void handleReturnDocument() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Trả Tài Liệu");
        dialog.setHeaderText("Nhập RecordID của tài liệu cần trả:");
        dialog.setContentText("RecordID:");
        
        dialog.showAndWait().ifPresent(recordId -> {
            try {
                userMan.returnBorrowRecord(recordId, LocalDate.now());
                populateRecordList();
                showAlert("Thành công", "Đã trả tài liệu thành công!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Lỗi", "Trả tài liệu thất bại: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
        
    }
}