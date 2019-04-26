import java.sql.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

class ConnectionManager {
    private static Connection con;

    private static Connection getConnection() {
        try {
            String driverName = "com.mysql.cj.jdbc.Driver";
            Class.forName(driverName);
            try {
                String username = ConnectionManager.getProp("db.user");
                String password = ConnectionManager.getProp("db.password");
                String url = "jdbc:mysql://" + ConnectionManager.getProp("db.url") +"/" + ConnectionManager.getProp("db.database");
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

    private static String getProp(String nameOfProp){
        // Make new properties object for saving
        Properties prop = new Properties();
        try (InputStream input = ConnectionManager.class.getResourceAsStream("resources/config.properties")) {
            //load a properties file from class path, inside static method
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(nameOfProp);
    }
}