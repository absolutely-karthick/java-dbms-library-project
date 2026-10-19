import java.sql.SQLException;
import java.util.List;

public class TestBookDAO {
    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();

        try {
            // Add a sample book
            Book book = new Book(
                "B001",
                "Wings of Fire",
                "A. P. J. Abdul Kalam",
                5,
                5
            );

            boolean added = bookDAO.addBook(book);

            if (added) {
                System.out.println("Book added successfully!");
            }

            // Display all books
            System.out.println("\n=== All Books ===");

            List<Book> books = bookDAO.getAllBooks();

            for (Book b : books) {
                System.out.println(b);
            }

        } catch (SQLException e) {
            System.out.println("Database operation failed:");
            System.out.println(e.getMessage());
        }
    }
}
