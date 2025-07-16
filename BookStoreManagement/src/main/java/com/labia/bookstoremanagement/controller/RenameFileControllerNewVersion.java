// file new name
import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class OwaspTop10VulnerabilitiesDemo extends HttpServlet {

    // A03:2021 - Injection (SQL Injection)
    public List<String> getBookInfo(String bookId) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM books WHERE book.id = '" + bookId + "'"; // vulnerable to SQLi
        ResultSet rs = stmt.executeQuery(query);
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString("email"));
        }
        return result;
    }
}
// file new name
import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class OwaspTop10VulnerabilitiesDemo extends HttpServlet {

    // A03:2021 - Injection (SQL Injection)
    public List<String> getBookInfo(String bookId) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM books WHERE book.id = '" + bookId + "'"; // vulnerable to SQLi
        ResultSet rs = stmt.executeQuery(query);
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString("email"));
        }
        return result;
    }



}
