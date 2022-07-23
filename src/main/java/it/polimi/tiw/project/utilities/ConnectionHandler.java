package it.polimi.tiw.project.utilities;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class is useful to handle the connection to the database.
 */
public class ConnectionHandler {

    /**
     * Returns the connection to the database.
     * @param context 				 the ServletContext} of the Servlet.
     * @return 						 the Connection to the database.
     * @throws UnavailableException  if the connection cannot be initialized.
     */
	public static Connection getConnection(ServletContext context) throws UnavailableException {
		Connection connection = null;
		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
		return connection;
	}

	
	/**
	 * Closes the connection to the database.
	 * @param connection			 the Connection to close.
	 * @throws SQLException			 if an error occurs while trying to close the connection.
	 */
	public static void closeConnection(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}
	
}