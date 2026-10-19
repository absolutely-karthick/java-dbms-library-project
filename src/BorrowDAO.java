
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // Create a borrowing record using a new connection
    public boolean addBorrowRecord(BorrowRecord record)
            throws SQLException {

        try (Connection con = Database_connection.getConnection()) {
            return addBorrowRecord(con, record);
        }
    }

    // Create a borrowing record using an existing connection
    public boolean addBorrowRecord(
            Connection con, BorrowRecord record)
            throws SQLException {

        String sql = "INSERT INTO borrow_records " +
                "(member_id, book_id, issue_date, return_date, days_kept, fine) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, record.getMemberId());
            ps.setString(2, record.getBookId());

            if (record.getIssueDate() == null) {
                ps.setNull(3, java.sql.Types.DATE);
            } else {
                ps.setDate(3, Date.valueOf(record.getIssueDate()));
            }

            if (record.getReturnDate() == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(record.getReturnDate()));
            }

            if (record.getDaysKept() == null) {
                ps.setNull(5, java.sql.Types.INTEGER);
            } else {
                ps.setInt(5, record.getDaysKept());
            }

            ps.setDouble(6, record.getFine());

            return ps.executeUpdate() > 0;
        }
    }

    // Get all borrowing records
    public List<BorrowRecord> getAllBorrowRecords()
            throws SQLException {

        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records ORDER BY record_id";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date issueSqlDate = rs.getDate("issue_date");
                Date returnSqlDate = rs.getDate("return_date");

                int days = rs.getInt("days_kept");
                Integer daysKept = rs.wasNull() ? null : days;

                records.add(new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getString("member_id"),
                        rs.getString("book_id"),
                        issueSqlDate == null ? null :
                                issueSqlDate.toLocalDate(),
                        returnSqlDate == null ? null :
                                returnSqlDate.toLocalDate(),
                        daysKept,
                        rs.getDouble("fine")
                ));
            }
        }

        return records;
    }

    // Return a book by updating its borrowing record
    public boolean returnBook(int recordId, LocalDate returnDate,
                              int daysKept, double fine)
            throws SQLException {

        String sql = "UPDATE borrow_records " +
                "SET return_date = ?, days_kept = ?, fine = ? " +
                "WHERE record_id = ? AND return_date IS NULL";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(returnDate));
            ps.setInt(2, daysKept);
            ps.setDouble(3, fine);
            ps.setInt(4, recordId);

            return ps.executeUpdate() > 0;
        }
    }
}
