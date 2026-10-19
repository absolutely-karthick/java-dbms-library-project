import java.sql.SQLException;

public class TestReturnBook {
    public static void main(String[] args) {
        LibraryService service = new LibraryService();

        // Replace 1 with the actual active record_id in your database.
        int recordId = 1;

        try {
            System.out.println(service.returnBook(recordId));
        } catch (SQLException e) {
            System.out.println("Return failed: " + e.getMessage());
        }
    }
}
