import java.sql.SQLException;
import java.util.List;

public class TestMemberDAO {
    public static void main(String[] args) {
        MemberDAO memberDAO = new MemberDAO();

        try {
            Member member = new Member("M001", "Arun");

            boolean added = memberDAO.addMember(member);

            if (added) {
                System.out.println("Member registered successfully!");
            }

            System.out.println("\n=== All Members ===");

            List<Member> members = memberDAO.getAllMembers();

            for (Member m : members) {
                System.out.println(m);
            }

        } catch (SQLException e) {
            System.out.println("Database operation failed:");
            System.out.println(e.getMessage());
        }
    }
}
