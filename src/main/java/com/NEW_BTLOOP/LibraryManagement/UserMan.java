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
