package it.polimi.tiw.project.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.User;

/**
 * This class manages the access to the database that contains registered users' information.
 */
public class UserDAO {
	private Connection connection;


	/**
	 * Class constructor.
	 * @param c				the Connection.
	 */
	public UserDAO(Connection c) {
		this.connection = c;
	}


	/**
	 * Gets user's ID given their nickname.
	 * Used in {@link MeetingDAO}'s method {@link MeetingDAO#createMeeting createMeeting}.
	 * @param username		user's nickname.
	 * @return				the corresponding ID.
	 * @throws SQLException	if there is an error while accessing the database.
	 */
	public int getIDByNick(String username) throws SQLException {
		
		String query = "SELECT userID FROM user WHERE username = ?";
		
		try(PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setString(1, username);
			
			try(ResultSet result = pstat.executeQuery()){
				//to be 100% sure that a row with that username exists in the database
				// even though it should be impossible to have the wrong username
				if (!result.isBeforeFirst()) {
					return -1;
				}
				
				result.next();
				return result.getInt("userID");
			}
		}

	}


	/**
	 * Gets the username given a user's ID.
	 * Used in {@link it.polimi.tiw.project.controllers.GoToHomepage GoToHomepage} servlet
	 * to display creator's username instead of their ID in the Homepage.
	 * @param id			user's ID.
	 * @return				the corresponding username.
	 * @throws SQLException if there is an error while accessing the database.
	 */
	//used in GoToHomepage to display creator's username instead of their id
	public String getNickByID(int id) throws SQLException {
		
		String query = "SELECT username FROM user WHERE userID = ?";
		
		try(PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setInt(1, id);
			
			try(ResultSet result = pstat.executeQuery()){
				//to be 100% sure that a row with that id exists in the database
				// even though it should be impossible to have the wrong id
				if(!result.isBeforeFirst()) {
					return null;
				}
				
				result.next();
				return result.getString("username");
			}
		}
	}


	/**
	 * Checks the presence of user's username and password in the database.
	 * @param username		the username.
	 * @param password		the password.
	 * @return				the corresponding User (if exists).
	 * @throws SQLException if there is an error while accessing the database.
	 */
	public User checkCredentials(String username, String password) throws SQLException {
		
		String query = "SELECT userID, email, name, surname, age, city FROM user WHERE username = ? AND password = ?";
		
		try(PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setString(1, username);
			pstat.setString(2, password);
			try(ResultSet result = pstat.executeQuery()){
				if (!result.isBeforeFirst()) { // no results, credential check failed
					return null;
				} else {
					result.next();
					User u = new User();
					u.setUsername(username);
					u.setID(result.getInt("userID"));
					u.setEmail(result.getString("email"));
					u.setName(result.getString("name"));
					u.setSurname(result.getString("surname"));
					u.setAge(result.getInt("age"));
					u.setCity(result.getString("city"));
					
					return u;
				}
			}
		}
		
	}


	/**
	 * Adds a new row to the user table with the information in the parameters.
	 * @param email			the email.
	 * @param username		the username.
	 * @param name			the name.
	 * @param surname		the surname.
	 * @param password		the password
	 * @param age			the age.
	 * @param city			the city.
	 * @return				the index of the newly created row.
	 * @throws SQLException if there is an error while accessing the database.
	 */
	public int createUser(String email, String username, String name, String surname, String password, int age, String city) throws SQLException {
		int code;
		
		String query = "INSERT into user (email, username, name, surname, password, age, city) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setString(1, email);
			pstat.setString(2, username);
			pstat.setString(3, name);
			pstat.setString(4, surname);
			pstat.setString(5, password);
			pstat.setInt(6, age);
			pstat.setString(7, city);
		
			code = pstat.executeUpdate();
		}

		return code;
	}


	/**
	 * Checks if a username is available or not.
	 * Available means that there are no rows in the user table containing that username.
	 * @param username		the username.
	 * @return				a boolean whose value is:
	 * 						<p>
	 * 						-{@code true} if the username is available;
	 * 						</p> <p>
	 * 						-{@code false} otherwise.
	 * 						</p>
	 * @throws SQLException if there is an error while accessing the database.
	 */
	public boolean checkAvailability(String username) throws SQLException {
		
		String query = "SELECT userID FROM user WHERE username = ?";
		
		try (PreparedStatement pstat = connection.prepareStatement(query)){
			pstat.setString(1, username);
			try(ResultSet result = pstat.executeQuery()){
				if (!result.isBeforeFirst()) { // no results, username not found -> can be used
					return true;
				} else {
					result.next();
					//maybe lines 177 and 178 are useless
					User u = new User();
					u.setID(result.getInt("userID"));
					
					return false;
				}
			}
		}
		
	}


	/**
	 * Gets users' information to display in RecordsPage.
	 * @return				a List of Users with the information needed.
	 * @throws SQLException	if there is an error while accessing the database.
	 */
	public List<User> getRegisteredUsers() throws SQLException {
		List<User> users = new ArrayList<>();
		
		String query = "SELECT userID, email, username, name, surname FROM user";
		
		try(PreparedStatement pstat = connection.prepareStatement(query)){
			try (ResultSet result = pstat.executeQuery()) {
				while (result.next()) {
					User u = new User();
					u.setID(result.getInt("userID"));
					u.setEmail(result.getString("email"));
					u.setUsername(result.getString("username"));
					u.setName(result.getString("name"));
					u.setSurname(result.getString("surname"));
					users.add(u);
				}
			}
		}
		return users;
	}
	
}