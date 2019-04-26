package Data.Database;

import java.sql.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The type Connection manager.
 */
class ConnectionManager {
    private static Connection con;

    /**
     * Make connection with the database, credentials can be found in the config.properties
     *
     * @return Connection
     */
    private Connection getConnection() {
        try {
            String driverName = "com.mysql.cj.jdbc.Driver";
            Class.forName(driverName);
            try {
                String username = this.getProp("db.user");
                String password = this.getProp("db.password");
                String url = "jdbc:mysql://" + this.getProp("db.url") +"/" + this.getProp("db.database");
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


    /**
     * Call result set.
     *
     * @param sql the sql to be called.
     * @return the result set
     * @throws SQLException the sql exception
     */
    ResultSet call(String sql) throws SQLException {
        Connection con;
        Statement stmt;
        ResultSet rs;

        con = this.getConnection();
        stmt = con.createStatement();
        assert stmt != null;
        rs = stmt.executeQuery(sql);
        return rs;
    }

    /**
     * Get property out of properties file.
     *
     * @param nameOfProp name of the property.
     * @return given property asked for
     */
    private String getProp(String nameOfProp){
        // Make new properties object for saving
        Properties prop = new Properties();
        try (InputStream input = ConnectionManager.class.getResourceAsStream("../../resources/config.properties")) {
            //load a properties file from class path, inside static method
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return prop.getProperty(nameOfProp);
    }
}