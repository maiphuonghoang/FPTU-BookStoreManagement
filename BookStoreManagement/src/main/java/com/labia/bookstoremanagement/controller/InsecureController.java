import java.sql.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class OwaspTop10VulnerabilitiesDemo extends HttpServlet {

    // A01:2021 - Broken Access Control
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userRole = request.getParameter("role");
        if ("admin".equals(userRole)) {
            response.getWriter().println("Access granted to admin panel");
        } else {
            response.getWriter().println("Access granted to user panel"); // No proper access control
        }
    }

    // A02:2021 - Cryptographic Failures (Insecure storage of passwords)
    public void storePassword(String password) throws Exception {
        FileWriter fw = new FileWriter("passwords.txt", true);
        fw.write(password + "\n"); // Password stored in plaintext
        fw.close();
    }

    // A03:2021 - Injection (SQL Injection)
    public List<String> getUserInfo(String username) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", "password");
        Statement stmt = conn.createStatement();
        String query = "SELECT * FROM users WHERE username = '" + username + "'"; // vulnerable to SQLi
        ResultSet rs = stmt.executeQuery(query);
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString("email"));
        }
        return result;
    }

    // A04:2021 - Insecure Design (no validation on critical logic)
    public boolean resetPassword(String token, String newPassword) {
        // No validation of token or user session
        return true; // Assumes reset successful
    }

    // A05:2021 - Security Misconfiguration
    public void printDebugInfo(HttpServletResponse response) throws IOException {
        response.getWriter().println("Debug mode ON"); // Should not be exposed in production
        response.getWriter().println("DB_URL=jdbc:mysql://localhost/test");
        response.getWriter().println("DB_USER=root");
        response.getWriter().println("DB_PASS=password");
    }

    // A06:2021 - Vulnerable and Outdated Components
    public void useOldLibrary() {
        // Assume this method uses Apache Commons Collections 3.2.1, known for deserialization flaws
        System.out.println("Using outdated library");
    }

    // A07:2021 - Identification and Authentication Failures
    public boolean login(String username, String password) {
        if ("admin".equals(username) && "123456".equals(password)) { // Weak hardcoded credentials
            return true;
        }
        return false;
    }

    // A08:2021 - Software and Data Integrity Failures
    public void updateApp(String filePath) throws IOException {
        File file = new File(filePath);
        // No integrity check (e.g., hash or signature) before using the file
        BufferedReader br = new BufferedReader(new FileReader(file));
        System.out.println("Updated with: " + br.readLine());
        br.close();
    }

    // A09:2021 - Security Logging and Monitoring Failures
    public void processPayment(String user, int amount) {
        // No logging or monitoring of transaction
        System.out.println("Processed payment for " + user);
    }

    // A10:2021 - Server-Side Request Forgery (SSRF)
    public void fetchMetadata(String urlStr) throws IOException {
        // No URL whitelist or validation
        InputStream in = new URL(urlStr).openStream(); // attacker can pass internal IP like http://169.254.169.254
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        System.out.println("Fetched: " + br.readLine());
        br.close();
    }
}
