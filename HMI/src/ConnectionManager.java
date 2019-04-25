import java.sql.*;

class ConnectionManager {
    private static Connection con;

    private static Connection getConnection() {
        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            try {
                String username = "root";
                String password = "";
                String url = "jdbc:mysql://localhost/wideworldimporters";
                con = DriverManager.getConnection(url, username, password);
            } catch (SQLException ex) {
                // log an exception. fro example:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception. for example:
            System.out.println("Driver not found.");
        }
        return con;
    }

    static ResultSet call(String sql) throws SQLException {
        Connection con;
        Statement stmt;
        ResultSet rs;

        con = ConnectionManager.getConnection();
        stmt = con.createStatement();
        assert stmt != null;
        rs = stmt.executeQuery(sql);
        return rs;
    }
}