import java.sql.SQLException;

public class TestLibraryService {

    public static void main(String[] args) {
        LibraryService libraryService = new LibraryService();

        try {
            String result = libraryService.issueBook("M001", "B001");
            System.out.println(result);
        } catch (SQLException e) {
            System.out.println("Issue failed: " + e.getMessage());
        }
    }
}
