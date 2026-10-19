
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryService {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    // Issue a book to a member
    public String issueBook(String memberId, String bookId)
            throws SQLException {

        try (Connection con = Database_connection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Check member exists using the same connection
                if (memberDAO.getMemberById(con, memberId) == null) {
                    con.rollback();
                    return "Member not found.";
                }

                // Lock book row and check availability
                Book book;

                String bookSql =
                        "SELECT * FROM books WHERE book_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {
                    ps.setString(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return "Book not found.";
                        }

                        book = new Book(
                                rs.getString("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("total_copies"),
                                rs.getInt("available_copies")
                        );
                    }
                }

                if (book.getAvailableCopies() <= 0) {
                    con.rollback();
                    return "No copies available.";
                }

                // Check active loans (maximum 3 per member)
                String countSql =
                        "SELECT COUNT(*) FROM borrow_records " +
                        "WHERE member_id = ? AND return_date IS NULL";

                try (PreparedStatement ps =
                             con.prepareStatement(countSql)) {
                    ps.setString(1, memberId);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();

                        if (rs.getInt(1) >= 3) {
                            con.rollback();
                            return "Member already has 3 active loans.";
                        }
                    }
                }

                BorrowRecord record = new BorrowRecord(
                        0, memberId, bookId,
                        LocalDate.now(), null, null, 0.0
                );

                if (!borrowDAO.addBorrowRecord(con, record)) {
                    con.rollback();
                    return "Could not create borrowing record.";
                }

                if (!bookDAO.updateAvailableCopies(
                        con, bookId, book.getAvailableCopies() - 1)) {
                    con.rollback();
                    return "Could not update book availability.";
                }

                con.commit();
                return "Book issued successfully!";

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Return a book and calculate the fine
    public String returnBook(int recordId) throws SQLException {

        try (Connection con = Database_connection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Lock the borrowing record
                String recordSql =
                        "SELECT * FROM borrow_records " +
                        "WHERE record_id = ? FOR UPDATE";

                String memberId;
                String bookId;
                LocalDate issueDate;
                LocalDate existingReturnDate;

                try (PreparedStatement ps =
                             con.prepareStatement(recordSql)) {
                    ps.setInt(1, recordId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return "Borrowing record not found.";
                        }

                        memberId = rs.getString("member_id");
                        bookId = rs.getString("book_id");

                        Date issueSqlDate = rs.getDate("issue_date");
                        Date returnSqlDate = rs.getDate("return_date");

                        issueDate = issueSqlDate == null
                                ? null : issueSqlDate.toLocalDate();

                        existingReturnDate = returnSqlDate == null
                                ? null : returnSqlDate.toLocalDate();
                    }
                }

                if (existingReturnDate != null) {
                    con.rollback();
                    return "This book has already been returned.";
                }

                if (issueDate == null) {
                    con.rollback();
                    return "Issue date is missing for this record.";
                }

                // Lock book row and check it exists
                Book book;

                String bookSql =
                        "SELECT * FROM books WHERE book_id = ? FOR UPDATE";

                try (PreparedStatement ps =
                             con.prepareStatement(bookSql)) {
                    ps.setString(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return "Book not found.";
                        }

                        book = new Book(
                                rs.getString("book_id"),
                                rs.getString("title"),
                                rs.getString("author"),
                                rs.getInt("total_copies"),
                                rs.getInt("available_copies")
                        );
                    }
                }

                LocalDate returnDate = LocalDate.now();

                // Count days inclusively: issue date is day 1
                long elapsedDays =
                        ChronoUnit.DAYS.between(issueDate, returnDate);

                if (elapsedDays < 0) {
                    con.rollback();
                    return "Return date cannot be before issue date.";
                }

                int daysKept = Math.toIntExact(elapsedDays + 1);

                // First 7 days free; ₹2 per additional day
                double fine = Math.max(0, daysKept - 7) * 2.0;

                // Update the borrowing record using this transaction
                String updateRecordSql =
                        "UPDATE borrow_records " +
                        "SET return_date = ?, days_kept = ?, fine = ? " +
                        "WHERE record_id = ? AND return_date IS NULL";

                try (PreparedStatement ps =
                             con.prepareStatement(updateRecordSql)) {
                    ps.setDate(1, Date.valueOf(returnDate));
                    ps.setInt(2, daysKept);
                    ps.setDouble(3, fine);
                    ps.setInt(4, recordId);

                    if (ps.executeUpdate() == 0) {
                        con.rollback();
                        return "Could not update borrowing record.";
                    }
                }

                // Restore one available copy
                if (book.getAvailableCopies() >= book.getTotalCopies()) {
                    con.rollback();
                    return "Available copies are already at the total count.";
                }

                if (!bookDAO.updateAvailableCopies(
                        con, bookId, book.getAvailableCopies() + 1)) {
                    con.rollback();
                    return "Could not update book availability.";
                }

                con.commit();

                return "Book returned successfully! Days kept: "
                        + daysKept + ", Fine: ₹" + String.format("%.2f", fine);

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
