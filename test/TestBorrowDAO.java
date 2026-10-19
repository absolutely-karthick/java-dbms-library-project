import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TestBorrowDAO {
    public static void main(String[] args) {
        BorrowDAO borrowDAO = new BorrowDAO();

        try {
            // Create a borrowing record for B001 and M001
            BorrowRecord record = new BorrowRecord(
                    0,                  // MySQL generates record_id
                    "M001",             // Member ID
                    "B001",             // Book ID
                    LocalDate.now(),    // Issue date
                    null,               // Not returned yet
                    null,               // Days kept not known yet
                    0.0                 // No fine yet
            );

            boolean added = borrowDAO.addBorrowRecord(record);

            if (added) {
                System.out.println("Borrow record added successfully!");
            }

            // Display all borrowing records
            System.out.println("\n=== Borrow Records ===");

            List<BorrowRecord> records =
                    borrowDAO.getAllBorrowRecords();

            for (BorrowRecord r : records) {
                System.out.println(r);
            }

        } catch (SQLException e) {
            System.out.println("Database operation failed:");
            System.out.println(e.getMessage());
        }
    }
}
