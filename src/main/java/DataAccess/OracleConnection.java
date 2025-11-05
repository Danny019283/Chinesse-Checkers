package DataAccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleConnection {

    protected Connection connection = null;

    public OracleConnection() {

    }

    protected void connect() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@//localhost:1521/xe";
            String username = "System";
            String password = "7R807hur";
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(OracleConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void disconnect() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        OracleConnection s = new OracleConnection();
        try {
            s.connect();
            System.out.println("Conexion");
            s.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
