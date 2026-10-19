
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    // Register a member using a new connection
    public boolean addMember(Member member) throws SQLException {
        try (Connection con = Database_connection.getConnection()) {
            return addMember(con, member);
        }
    }

    // Register a member using an existing connection
    public boolean addMember(Connection con, Member member)
            throws SQLException {

        String sql =
                "INSERT INTO members (member_id, name) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, member.getMemberId());
            ps.setString(2, member.getName());

            return ps.executeUpdate() > 0;
        }
    }

    // Get all members
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members";

        try (Connection con = Database_connection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                members.add(new Member(
                        rs.getString("member_id"),
                        rs.getString("name")
                ));
            }
        }

        return members;
    }

    // Find a member by ID using a new connection
    public Member getMemberById(String memberId) throws SQLException {
        try (Connection con = Database_connection.getConnection()) {
            return getMemberById(con, memberId);
        }
    }

    // Find a member by ID using an existing connection
    public Member getMemberById(Connection con, String memberId)
            throws SQLException {

        String sql = "SELECT * FROM members WHERE member_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Member(
                            rs.getString("member_id"),
                            rs.getString("name")
                    );
                }
            }
        }

        return null;
    }
}
