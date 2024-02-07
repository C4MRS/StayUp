package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
public class ConnectionPool {
    private static List<Connection> freeDbConnections;

    static {
        freeDbConnections = new LinkedList<Connection>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("DB driver not found:"+ e.getMessage());
        }
    }

    private static synchronized Connection createDBConnection() throws SQLException {
        Connection newConnection = null;
        /*final String DB_URL_with_SSL = "jdbc:mysql://localhost:3306/flyhigh?verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String username = "root";
        String password = "admin";*/
        final String DB_URL_with_SSL = "jdbc:mysql://db4free.net:3306/stayup?autoReconnect=true&useSSL=false";
        String username = "stayupadmin";
        String password = "stayupadm!n";

        newConnection = DriverManager.getConnection(DB_URL_with_SSL, username, password);

        newConnection.setAutoCommit(true);
        return newConnection;
    }


    public static synchronized Connection getConnection() throws SQLException {
        Connection connection;

        if (!freeDbConnections.isEmpty()) {
            connection = (Connection) freeDbConnections.get(0);
            freeDbConnections.remove(0);

            try {
                if (connection.isClosed())
                    connection = getConnection();
            } catch (SQLException e) {
                connection.close();
                connection = getConnection();
            }
        } else {
            connection = createDBConnection();
        }

        return connection;
    }

    public static synchronized void releaseConnection(Connection connection) throws SQLException {
        if(connection != null) freeDbConnections.add(connection);
    }
}
