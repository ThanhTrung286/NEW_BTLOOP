package com.NEW_BTLOOP.LibraryManagement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Library {

    // Kết nối CSDL

    private static final String DB_URL = "jdbc:mysql://localhost:3306/library";
    private static final String USER = "root";
    private static final String PASS = "root";

    private Connection conn;

    public Library() throws SQLException {
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        UserMan userManager = new UserMan(conn);
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

    public boolean recordExists(String tableName, String id) throws SQLException {
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
        String id = generateNextID("BOK", "document");
        book.setId(id);
        conn.setAutoCommit(false);
        try {
            String docSql = "INSERT INTO document (ID, Title, Author, Total, Available, Year, DocType) VALUES (?, ?, ?, ?, ?, ?, 'BOOK')";
            try (PreparedStatement stmt = conn.prepareStatement(docSql)) {
                stmt.setString(1, book.getId());
                stmt.setString(2, book.getTitle());
                stmt.setString(3, book.getAuthor());
                stmt.setInt(4, book.getTotal());
                stmt.setInt(5, book.getAvail());
                stmt.setInt(6, book.getYear());
                stmt.executeUpdate();
            }

            String bookSql = "INSERT INTO book (ID, Publisher, NumberOfPages, Genre, ISBN) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(bookSql)) {
                stmt.setString(1, book.getId());
                stmt.setString(2, book.getPublisher());
                stmt.setInt(3, book.getNumberOfPages());
                stmt.setString(4, book.getGenre());
                stmt.setString(5, book.getISBN());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Document getDocumentById(String id) throws SQLException {
    String sql = "SELECT d.*, b.Publisher, b.NumberOfPages, b.Genre, b.ISBN, " +
                 "t.University, t.Supervisor, t.Department " +
                 "FROM document d " +
                 "LEFT JOIN book b ON d.ID = b.ID " +
                 "LEFT JOIN thesis t ON d.ID = t.ID " +
                 "WHERE d.ID = ?";

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String docType = rs.getString("DocType");
                if ("BOOK".equalsIgnoreCase(docType)) {
                    Book b = new Book();
                    b.setId(rs.getString("ID"));
                    b.setTitle(rs.getString("Title"));
                    b.setAuthor(rs.getString("Author"));
                    b.setYear(rs.getInt("Year"));
                    b.setTotal(rs.getInt("Total"));
                    b.setAvail(rs.getInt("Available"));
                    b.setPublisher(rs.getString("Publisher"));
                    b.setNumberOfPages(rs.getInt("NumberOfPages"));
                    b.setGenre(rs.getString("Genre"));
                    b.setISBN(rs.getString("ISBN"));
                    return b;
                } else if ("THESIS".equalsIgnoreCase(docType)) {
                    Thesis t = new Thesis();
                    t.setId(rs.getString("ID"));
                    t.setTitle(rs.getString("Title"));
                    t.setAuthor(rs.getString("Author"));
                    t.setYear(rs.getInt("Year"));
                    t.setTotal(rs.getInt("Total"));
                    t.setAvail(rs.getInt("Available"));
                    t.setUniversity(rs.getString("University"));
                    return t;
                }
            }
        }
    }
    return null; // No match found
}

    public void updateBook(Book book) throws SQLException {
        if (!recordExists("document", book.getId())) {
            throw new SQLException("Book ID not found: " + book.getId());
        }

        conn.setAutoCommit(false);
        try {
            String docSql = "UPDATE document SET Title=?, Author=?, Total=?, Available=?, Year=? WHERE ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(docSql)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setInt(3, book.getTotal());
                stmt.setInt(4, book.getAvail());
                stmt.setInt(5, book.getYear());
                stmt.setString(6, book.getId());
                stmt.executeUpdate();
            }

            String bookSql = "UPDATE book SET Publisher=?, NumberOfPages=?, Genre=?, ISBN=? WHERE ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(bookSql)) {
                stmt.setString(1, book.getPublisher());
                stmt.setInt(2, book.getNumberOfPages());
                stmt.setString(3, book.getGenre());
                stmt.setString(4, book.getISBN());
                stmt.setString(5, book.getId());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Book> listBooks() throws SQLException {
        List<Document> docs = searchDocuments(Map.of("DocType", "BOOK"));
        List<Book> books = new ArrayList<>();
        for (Document doc : docs) {
            if (doc instanceof Book) {
                books.add((Book) doc);
            }
        }
        return books;
    }

    public void addThesis(Thesis thesis) throws SQLException {
        String id = generateNextID("THS", "document");
        thesis.setId(id);

        conn.setAutoCommit(false);
        try {
            String docSql = "INSERT INTO document (ID, Title, Author, Total, Available, Year, DocType) VALUES (?, ?, ?, ?, ?, ?, 'THESIS')";
            try (PreparedStatement stmt = conn.prepareStatement(docSql)) {
                stmt.setString(1, thesis.getId());
                stmt.setString(2, thesis.getTitle());
                stmt.setString(3, thesis.getAuthor());
                stmt.setInt(4, thesis.getTotal());
                stmt.setInt(5, thesis.getAvail());
                stmt.setInt(3, thesis.getYear());
                stmt.executeUpdate();
            }

            String thesisSql = "INSERT INTO thesis (ID, University, Supervisor, Department) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(thesisSql)) {
                stmt.setString(1, thesis.getId());
                stmt.setString(2, thesis.getUniversity());
                stmt.setString(3, thesis.getSupervisor());
                stmt.setString(4, thesis.getDepartment());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void updateThesis(Thesis thesis) throws SQLException {
        if (!recordExists("document", thesis.getId())) {
            throw new SQLException("Thesis ID not found: " + thesis.getId());
        }

        conn.setAutoCommit(false);
        try {
            String docSql = "UPDATE document SET Title=?, Author=?, Total=?, Available=?, Year=? WHERE ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(docSql)) {
                stmt.setString(1, thesis.getTitle());
                stmt.setString(2, thesis.getAuthor());
                stmt.setInt(3, thesis.getTotal());
                stmt.setInt(4, thesis.getAvail());
                stmt.setInt(5, thesis.getYear());
                stmt.setString(6, thesis.getId());
                stmt.executeUpdate();
            }

            String thesisSql = "UPDATE thesis SET University=?, Supervisor=?, Department=? WHERE ID=?";
            try (PreparedStatement stmt = conn.prepareStatement(thesisSql)) {
                stmt.setString(1, thesis.getUniversity());
                stmt.setString(2, thesis.getSupervisor());
                stmt.setString(3, thesis.getDepartment());
                stmt.setString(4, thesis.getId());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Thesis> listTheses() throws SQLException {
        List<Document> docs = searchDocuments(Map.of("DocType", "THESIS"));
        List<Thesis> theses = new ArrayList<>();
        for (Document doc : docs) {
            if (doc instanceof Thesis) {
                theses.add((Thesis) doc);
            }
        }
        return theses;
    }

    public void updateAvail(String id, int newAvail) throws SQLException {
        if (!recordExists("document", id)) {
            throw new SQLException("Document ID not found: " + id);
        }
        String sql = "UPDATE document SET Available = ? WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newAvail);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public void updateTotal(String id, int newTotal) throws SQLException {
        if (!recordExists("document", id)) {
            throw new SQLException("Document ID not found: " + id);
        }
        String sql = "UPDATE document SET Available = ? WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newTotal);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public void deleteDoc(String id) throws SQLException {
        if (!recordExists("document", id)) {
            throw new SQLException("Document ID not found: " + id);
        }
        String sql = "DELETE FROM document WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Document> searchDocuments(Map<String, String> criteria) throws SQLException {
        List<Document> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT d.*, b.Publisher, b.NumberOfPages, b.Genre, b.ISBN, " +
                        "t.University, t.Supervisor, t.Department " +
                        "FROM document d " +
                        "LEFT JOIN book b ON d.ID = b.ID " +
                        "LEFT JOIN thesis t ON d.ID = t.ID WHERE 1=1");

        List<Object> params = new ArrayList<>();
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String col = entry.getKey();
            String val = entry.getValue();

            boolean isNumeric = val.matches("\\d+");
            if (isNumeric && !col.equalsIgnoreCase("ISBN")) {
                sql.append(" AND ").append(col).append(" = ?");
                params.add(Integer.parseInt(val));
            } else {
                sql.append(" AND LOWER(").append(col).append(") LIKE ?");
                params.add("%" + val.toLowerCase() + "%");
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String docType = rs.getString("DocType");
                    if ("BOOK".equalsIgnoreCase(docType)) {
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
                    } else if ("THESIS".equalsIgnoreCase(docType)) {
                        Thesis t = new Thesis();
                        t.setId(rs.getString("ID"));
                        t.setTitle(rs.getString("Title"));
                        t.setAuthor(rs.getString("Author"));
                        t.setTotal(rs.getInt("Total"));
                        t.setAvail(rs.getInt("Available"));
                        t.setUniversity(rs.getString("University"));
                        t.setSupervisor(rs.getString("Supervisor"));
                        t.setDepartment(rs.getString("Department"));
                        t.setYear(rs.getInt("Year"));
                        results.add(t);
                    }
                }
            }
        }
        return results;
    }

    public List<Document> generalSearch(String keyword) throws SQLException {
        String lowerKeyword = "%" + keyword.toLowerCase() + "%";
        return searchDocuments(Map.of(
                "Title", keyword,
                "Author", keyword,
                "Publisher", keyword,
                "Genre", keyword,
                "University", keyword,
                "ISBN", keyword));
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
                user.setBorrowedLimit(rs.getInt("BorrowedLimit"));
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

    public void insertBorrowRecord(BorrowRecord record) throws SQLException {
        String id = generateNextID("BRW", "borrow_record");
        record.setRecordID(id);

        conn.setAutoCommit(false);
        try {
            String sql = "INSERT INTO borrow_record (RecordID, UserID, DocumentID, BorrowDate, ExpectedReturnDate) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, record.getRecordID());
                stmt.setString(2, record.getUserID());
                stmt.setString(3, record.getDocumentID());
                stmt.setDate(4, Date.valueOf(record.getBorrowDate()));
                stmt.setDate(5, Date.valueOf(record.getExpectedReturnDate()));
                stmt.executeUpdate();
            }

            String updateDoc = "UPDATE document SET Available = Available - 1 WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDoc)) {
                stmt.setString(1, record.getDocumentID());
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void updateBorrowReturnDate(String recordID, LocalDate returnDate) throws SQLException {
        if (!recordExists("borrow_record", recordID)) {
            throw new SQLException("Borrow record not found: " + recordID);
        }
        conn.setAutoCommit(false);
        try {
            String sql = "UPDATE borrow_record SET ActualReturnDate=? WHERE RecordID=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(returnDate));
                stmt.setString(2, recordID);
                stmt.executeUpdate();
            }

            String docSql = "UPDATE document SET Available = Available + 1 WHERE ID = (SELECT DocumentID FROM borrow_record WHERE RecordID=?)";
            try (PreparedStatement stmt = conn.prepareStatement(docSql)) {
                stmt.setString(1, recordID);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<BorrowRecord> getBorrowRecordsForUser(String userID) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record WHERE UserID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setRecordID(rs.getString("RecordID"));
                    br.setUserID(rs.getString("UserID"));
                    br.setDocumentID(rs.getString("DocumentID"));
                    br.setBorrowDate(rs.getDate("BorrowDate").toLocalDate());
                    br.setExpectedReturnDate(rs.getDate("ExpectedReturnDate").toLocalDate());
                    Date actual = rs.getDate("ActualReturnDate");
                    if (actual != null)
                        br.setActualReturnDate(actual.toLocalDate());
                    records.add(br);
                }
            }
        }
        return records;
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