package com.NEW_BTLOOP.LibraryManagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Library {

    // Kết nối CSDL

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASS = "matkhau_moi_cua_ban";

    private Connection conn;

    public Library() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }

    // Tự động tạo ID tài liệu

    private String generateNextID(String prefix, String tableName) throws SQLException {
        String sql = "SELECT ID FROM " + tableName + " WHERE ID LIKE ? ORDER BY ID DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String lastId = rs.getString("ID");
                    int number = Integer.parseInt(lastId.substring(3));
                    return String.format("%s%06d", prefix, number + 1);
                } else {
                    return prefix + "000001";
                }
            }
        }
    }

    private boolean checkDocExists(String tableName, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Thêm sách
    public void addBook(Book book) throws SQLException {
        String id = generateNextID("BOK", "book");
        book.setId(id);
        String sql = "INSERT INTO book (ID, ISBN, Title, Author, Publisher, Genre, Year, NumberOfPages, Total, Available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getId());
            stmt.setString(2, book.getISBN());
            stmt.setString(3, book.getTitle());
            stmt.setString(4, book.getAuthor());
            stmt.setString(5, book.getPublisher());
            stmt.setString(6, book.getGenre());
            stmt.setInt(7, book.getYear());
            stmt.setInt(8, book.getNumberOfPages());
            stmt.setInt(9, book.getTotal());
            stmt.setInt(10, book.getAvail());
            stmt.executeUpdate();
        }
    }

    
    public Book getBookByID(String id) throws SQLException {
        String sql = "SELECT * FROM book WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getString("ID"));
                    b.setISBN(rs.getString("ISBN"));
                    b.setTitle(rs.getString("Title"));
                    b.setAuthor(rs.getString("Author"));
                    b.setPublisher(rs.getString("Publisher"));
                    b.setGenre(rs.getString("Genre"));
                    b.setYear(rs.getInt("Year"));
                    b.setNumberOfPages(rs.getInt("NumberOfPages"));
                    b.setTotal(rs.getInt("Total"));
                    b.setAvail(rs.getInt("Available"));
                    return b;
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy sách
    }

    public void updateBook(Book book) throws SQLException {
        if (!checkDocExists("book", book.getId())) {
            throw new SQLException("Book ID not found: " + book.getId());
        }
        String sql = "UPDATE book SET Title=?, Author=?, Total=?, Available=?, Publisher=?, NumberOfPages=?, Genre=?, Year=?, ISBN=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getTotal());
            stmt.setInt(4, book.getAvail());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getNumberOfPages());
            stmt.setString(7, book.getGenre());
            stmt.setInt(8, book.getYear());
            stmt.setString(9, book.getISBN());
            stmt.setString(10, book.getId());
            stmt.executeUpdate();
        }
    }

    public List<Book> listBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getString("ID"));
                b.setTitle(rs.getString("Title"));
                b.setAuthor(rs.getString("Author"));
                b.setTotal(rs.getInt("Total"));
                b.setAvail(rs.getInt("Available"));
                b.setPublisher(rs.getString("Publisher"));
                b.setNumberOfPages(rs.getInt("NumberOfPages"));
                b.setGenre(rs.getString("Genre"));
                b.setYear(rs.getInt("Year"));
                b.setISBN(rs.getString("ISBN"));
                books.add(b);
            }
        }
        return books;
    }

    public void addThesis(Thesis thesis) throws SQLException {
        String id = generateNextID("THS", "thesis");
        thesis.setId(id);

        String sql = "INSERT INTO thesis (ID, Title, Author, Supervisor, Department, University, Year, Total, Available) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, thesis.getId());
            stmt.setString(2, thesis.getTitle());
            stmt.setString(3, thesis.getAuthor());
            stmt.setString(4, thesis.getSupervisor());
            stmt.setString(5, thesis.getDepartment());
            stmt.setString(6, thesis.getUniversity());
            stmt.setInt(7, thesis.getYear());
            stmt.setInt(8, thesis.getTotal());
            stmt.setInt(9, thesis.getAvail());
            stmt.executeUpdate();
        }
    }

    public void updateThesis(Thesis thesis) throws SQLException {
        if (!checkDocExists("thesis", thesis.getId())) {
            throw new SQLException("Thesis ID not found: " + thesis.getId());
        }
        String sql = "UPDATE thesis SET Title=?, Author=?, Total=?, Available=?, University=?, Year=? WHERE ID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, thesis.getTitle());
            stmt.setString(2, thesis.getAuthor());
            stmt.setInt(3, thesis.getTotal());
            stmt.setInt(4, thesis.getAvail());
            stmt.setString(5, thesis.getUniversity());
            stmt.setInt(6, thesis.getYear());
            stmt.setString(7, thesis.getId());
            stmt.executeUpdate();
        }
    }

    public List<Thesis> listTheses() throws SQLException {
        List<Thesis> theses = new ArrayList<>();
        String sql = "SELECT * FROM thesis";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Thesis t = new Thesis();
                t.setId(rs.getString("ID"));
                t.setTitle(rs.getString("Title"));
                t.setAuthor(rs.getString("Author"));
                t.setSupervisor(rs.getString("Supervisor"));
                t.setDepartment(rs.getString("Department"));
                t.setTotal(rs.getInt("Total"));
                t.setAvail(rs.getInt("Available"));
                t.setUniversity(rs.getString("University"));
                t.setYear(rs.getInt("Year"));
                theses.add(t);
            }
        }
        return theses;
    }

    public void updateAvail(String id, int newAvail) throws SQLException {
        String table;
        if (id.substring(0, 3) == "BOK") {
            if (!checkDocExists("book", id)) {
                throw new SQLException("Book ID not found: " + id);
            }
            table = "book";
        }

        else {
            if (!checkDocExists("thesis", id)) {
                throw new SQLException("Thesis ID not found: " + id);
            }
            table = "thesis";
        }
        String sql = "UPDATE " + table + " SET Available = ? WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newAvail);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateTotal(String id, int newTotal) throws SQLException {
        String table;
        if (id.substring(0, 3) == "BOK") {
            if (!checkDocExists("book", id)) {
                throw new SQLException("Book ID not found: " + id);
            }
            table = "book";
        }

        else {
            if (!checkDocExists("thesis", id)) {
                throw new SQLException("Thesis ID not found: " + id);
            }
            table = "thesis";
        }
        String sql = "UPDATE " + table + " SET Available = ? WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTotal);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public void deleteDoc(String id) throws SQLException {
        String table;
    // Sửa lỗi: Sử dụng .equals() để so sánh chuỗi
        if (id.substring(0, 3).equals("BOK")) {
            if (!checkDocExists("book", id)) {
                throw new SQLException("Book ID not found: " + id);
            }
            table = "book";
        }
        else if (id.substring(0, 3).equals("THS")) {
            if (!checkDocExists("thesis", id)) {
                throw new SQLException("Thesis ID not found: " + id);
            }
            table = "thesis";
        } else {
            throw new SQLException("Invalid document ID prefix: " + id);
        }
    
        String sql = "DELETE FROM " + table + " WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Document> searchBookByCriteria(Map<String, String> criteria) throws SQLException {
        List<Document> results = new ArrayList<>();
        StringBuilder bookQuery = new StringBuilder("SELECT * FROM book WHERE 1=1");
        List<Object> bookValues = new ArrayList<>();

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue();
            switch (key) {
                case "id", "title", "author", "genre", "isbn" -> {
                    bookQuery.append(" AND ").append(key).append(" LIKE ?");
                    bookValues.add("%" + value + "%");
                }
                case "year", "total", "available", "numberofpages" -> {
                    bookQuery.append(" AND ").append(key).append(" = ?");
                    bookValues.add(Integer.parseInt(value));
                }
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(bookQuery.toString())) {
            for (int i = 0; i < bookValues.size(); i++) {
                stmt.setObject(i + 1, bookValues.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book b = new Book();
                    b.setId(rs.getString("ID"));
                    b.setTitle(rs.getString("Title"));
                    b.setAuthor(rs.getString("Author"));
                    b.setTotal(rs.getInt("Total"));
                    b.setAvail(rs.getInt("Available"));
                    b.setPublisher(rs.getString("Publisher"));
                    b.setNumberOfPages(rs.getInt("NumberOfPages"));
                    b.setGenre(rs.getString("Genre"));
                    b.setYear(rs.getInt("Year"));
                    b.setISBN(rs.getString("ISBN"));
                    results.add(b);
                }
            }

        }
        return results;
    }

    // Tìm luận án
    public List<Document> searchThesesByCriteria(Map<String, String> criteria) throws SQLException {
        List<Document> results = new ArrayList<>();
        StringBuilder thesisQuery = new StringBuilder("SELECT * FROM thesis WHERE 1=1");
        List<Object> thesisValues = new ArrayList<>();

        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue();
            switch (key) {
                case "id", "title", "author", "university" -> {
                    thesisQuery.append(" AND ").append(key).append(" LIKE ?");
                    thesisValues.add("%" + value + "%");
                }
                case "year", "total", "available" -> {
                    thesisQuery.append(" AND ").append(key).append(" = ?");
                    thesisValues.add(Integer.parseInt(value));
                }
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(thesisQuery.toString())) {
            for (int i = 0; i < thesisValues.size(); i++) {
                stmt.setObject(i + 1, thesisValues.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Thesis t = new Thesis();
                    t.setId(rs.getString("ID"));
                    t.setTitle(rs.getString("Title"));
                    t.setAuthor(rs.getString("Author"));
                    t.setTotal(rs.getInt("Total"));
                    t.setAvail(rs.getInt("Available"));
                    t.setUniversity(rs.getString("University"));
                    t.setYear(rs.getInt("Year"));
                    results.add(t);
                }
            }
        }

        return results;
    }

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO user (UserID, Name, Email, BorrowedDoc, BorrowedLimit) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUserID());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getBorrowedDocuments());
            stmt.setInt(5, user.getBorrowedLimit());
            stmt.executeUpdate();
        }
    }

    public List<User> listUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getString("UserID"));
                user.setName(rs.getString("Name"));
                user.setEmail(rs.getString("Email"));
                user.setBorrowedDocuments(rs.getInt("BorrowedDoc"));
                user.setBorrowedLimit(rs.getInt("BorrowenLimit"));
                users.add(user);
            }
        }
        return users;
    }

    private boolean checkUserExists(String tableName, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void updateBorrowedDocuments(String userId, int newCount) throws SQLException {
        String sql = "UPDATE user SET BorrowedDoc = ? WHERE UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newCount);
            stmt.setString(2, userId);
            stmt.executeUpdate();
        }
    }

    public void deleteUser(String userId) throws SQLException {
        if (!checkUserExists("user", userId)) {
                throw new SQLException("User ID not found: " + userId);
            }
        String sql = "DELETE FROM user WHERE UserID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        }
    }
}