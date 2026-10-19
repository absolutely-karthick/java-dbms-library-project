
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    // Add a new book
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books " +
                "(book_id, title, author, total_copies, available_copies) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getTotalCopies());
            ps.setInt(5, book.getAvailableCopies());

            return ps.executeUpdate() > 0;
        }
    }

    // Get all books
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(new Book(
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                ));
            }
        }

        return books;
    }

    // Find a book by ID
    public Book getBookById(String bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                            rs.getString("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("total_copies"),
                            rs.getInt("available_copies")
                    );
                }
            }
        }

        return null;
    }

    // Search books by title keyword
    public List<Book> searchBooksByTitle(String keyword)
            throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getString("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("total_copies"),
                            rs.getInt("available_copies")
                    ));
                }
            }
        }

        return books;
    }

    // Update available copies using a new connection
    public boolean updateAvailableCopies(String bookId, int copies)
            throws SQLException {
        String sql = "UPDATE books SET available_copies = ? " +
                "WHERE book_id = ?";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, copies);
            ps.setString(2, bookId);

            return ps.executeUpdate() > 0;
        }
    }

    // Update available copies using an existing connection.
    // The service can use this method inside a transaction.
    public boolean updateAvailableCopies(
            Connection con, String bookId, int copies)
            throws SQLException {

        String sql = "UPDATE books SET available_copies = ? " +
                "WHERE book_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, copies);
            ps.setString(2, bookId);

            return ps.executeUpdate() > 0;
        }
    }
}
