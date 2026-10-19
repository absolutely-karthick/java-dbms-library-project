
import java.time.LocalDate;

public class BorrowRecord {
    private int recordId;
    private String memberId;
    private String bookId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private Integer daysKept;
    private double fine;

    public BorrowRecord(int recordId, String memberId, String bookId,
                        LocalDate issueDate, LocalDate returnDate,
                        Integer daysKept, double fine) {
        this.recordId = recordId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.daysKept = daysKept;
        this.fine = fine;
    }

    public int getRecordId() {
        return recordId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getBookId() {
        return bookId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public Integer getDaysKept() {
        return daysKept;
    }

    public double getFine() {
        return fine;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setDaysKept(Integer daysKept) {
        this.daysKept = daysKept;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    @Override
    public String toString() {
        return "Record ID: " + recordId
                + ", Member ID: " + memberId
                + ", Book ID: " + bookId
                + ", Issue Date: " + issueDate
                + ", Return Date: " + returnDate
                + ", Days Kept: " + daysKept
                + ", Fine: " + fine;
    }
}
