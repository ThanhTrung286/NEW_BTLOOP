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
                stmt.setInt(6, thesis.getYear());
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
        List<Document> results = new ArrayList<>();
        String lowerKeyword = "%" + keyword.toLowerCase() + "%";

        String sql = "SELECT d.*, b.Publisher, b.NumberOfPages, b.Genre, b.ISBN, " +
                "t.University " +
                "FROM document d " +
                "LEFT JOIN book b ON d.ID = b.ID " +
                "LEFT JOIN thesis t ON d.ID = t.ID " +
                "WHERE LOWER(d.Title) LIKE ? " +
                "   OR LOWER(d.Author) LIKE ? " +
                "   OR LOWER(b.Publisher) LIKE ? " +
                "   OR LOWER(b.Genre) LIKE ? " +
                "   OR LOWER(b.ISBN) LIKE ? " +
                "   OR LOWER(t.University) LIKE ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, lowerKeyword);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                        results.add(b);
                    } else if ("THESIS".equalsIgnoreCase(docType)) {
                        Thesis t = new Thesis();
                        t.setId(rs.getString("ID"));
                        t.setTitle(rs.getString("Title"));
                        t.setAuthor(rs.getString("Author"));
                        t.setYear(rs.getInt("Year"));
                        t.setTotal(rs.getInt("Total"));
                        t.setAvail(rs.getInt("Available"));
                        t.setUniversity(rs.getString("University"));
                        results.add(t);
                    }
                }
            }
        }
        return results;
    }

    
}