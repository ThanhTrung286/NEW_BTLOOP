package com.NEW_BTLOOP.LibraryManagement;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserMan {
    private final Connection conn;

    public UserMan(Connection conn) {
        this.conn = conn;
    }

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

    // Mượn tài liệu
    public String insertBorrowRecord(String userId, String documentId, int borrowDays) throws SQLException {
        // Generate RecordID
        String recordId = generateNextID("BRW", "borrow_record");

        LocalDate borrowDate = LocalDate.now();
        LocalDate expectedReturnDate = borrowDate.plusDays(borrowDays);

        String sql = "INSERT INTO borrow_record (RecordID, UserID, DocumentID, BorrowDate, ExpectedReturnDate) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recordId);
            stmt.setString(2, userId);
            stmt.setString(3, documentId);
            stmt.setDate(4, Date.valueOf(borrowDate));
            stmt.setDate(5, Date.valueOf(expectedReturnDate));
            stmt.executeUpdate();
        }

        // Reduce available count in document table
        String updateDoc = "UPDATE document SET Available = Available - 1 WHERE ID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateDoc)) {
            stmt.setString(1, documentId);
            stmt.executeUpdate();
        }
        return recordId;
    }

    // Trả tài liệu
    public void returnBorrowRecord(String recordId, LocalDate actualReturnDate) throws SQLException {
        // If no date provided, default to today
        LocalDate returnDate = (actualReturnDate != null) ? actualReturnDate : LocalDate.now();

        // Check if already returned
        String checkSql = "SELECT DocumentID, ActualReturnDate FROM borrow_record WHERE RecordID = ?";
        String docId = null;
        try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
            stmt.setString(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date actual = rs.getDate("ActualReturnDate");
                    if (actual != null) {
                        throw new SQLException("This borrow record has already been returned: " + recordId);
                    }
                    docId = rs.getString("DocumentID");
                } else {
                    throw new SQLException("Borrow record not found: " + recordId);
                }
            }
        }

        // Update ActualReturnDate
        String sql = "UPDATE borrow_record SET ActualReturnDate = ? WHERE RecordID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(returnDate));
            stmt.setString(2, recordId);
            stmt.executeUpdate();
        }

        // Increase available count back
        if (docId != null) {
            String updateDoc = "UPDATE document SET Available = Available + 1 WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateDoc)) {
                stmt.setString(1, docId);
                stmt.executeUpdate();
            }
        }
    }

    // Tìm bằng userID
    public List<BorrowRecord> searchBorrowRecordsByUser(String userId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record WHERE UserID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setRecordID(rs.getString("RecordID"));
                    br.setUserID(rs.getString("UserID"));
                    br.setDocumentID(rs.getString("DocumentID"));
                    br.setBorrowDate(rs.getDate("BorrowDate").toLocalDate());
                    br.setExpectedReturnDate(rs.getDate("ExpectedReturnDate").toLocalDate());
                    Date actual = rs.getDate("ActualReturnDate");
                    if (actual != null) {
                        br.setActualReturnDate(actual.toLocalDate());
                    }
                    records.add(br);
                }
            }
        }
        return records;
    }

    // Tìm bằng docID
    public List<BorrowRecord> searchBorrowRecordsByDocument(String documentId) throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record WHERE DocumentID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BorrowRecord br = new BorrowRecord();
                    br.setRecordID(rs.getString("RecordID"));
                    br.setUserID(rs.getString("UserID"));
                    br.setDocumentID(rs.getString("DocumentID"));
                    br.setBorrowDate(rs.getDate("BorrowDate").toLocalDate());
                    br.setExpectedReturnDate(rs.getDate("ExpectedReturnDate").toLocalDate());
                    Date actual = rs.getDate("ActualReturnDate");
                    if (actual != null) {
                        br.setActualReturnDate(actual.toLocalDate());
                    }
                    records.add(br);
                }
            }
        }
        return records;
    }

    public List<BorrowRecord> listBorrowRecords() throws SQLException {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_record";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
