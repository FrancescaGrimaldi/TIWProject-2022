package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.Result;

import it.polimi.tiw.project.beans.User;

/**
 * This class manages the access to the database containing registered users' informations.
 */
public class UserDAO {
	private Connection connection;
	
	
	public UserDAO(Connection c) {
		this.connection = c;
	}
	
	
	public List<User> findAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		
		String query = "SELECT * FROM tiwproject.user";
		
		try(PreparedStatement pstat = connection.prepareStatement(query);){
			try (ResultSet result = pstat.executeQuery();) {
				while (result.next()) {
					User u = new User();
					u.setID(result.getInt("userID"));
					u.setEmail(result.getString("email"));
					u.setUsername(result.getString("username"));
					u.setName(result.getString("name"));
					u.setSurname(result.getString("surname"));
					u.setPassword(result.getString("password"));
					u.setAge(result.getInt("age"));
					u.setCity(result.getString("city"));
					users.add(u);
				}
			}
		}
		return users;
	}
	
	//don't know if it's useful
	public int getIDByNick(String username) throws SQLException {
		
		String query = "SELECT userID FROM tiwproject.user WHERE username = ?";
		
		try(PreparedStatement pstat = connection.prepareStatement(query);){
			pstat.setString(1, username);
			
			try(ResultSet result = pstat.executeQuery();){
				if (!result.isBeforeFirst()) {		//to be 100% sure even though it should be impossible to have the wrong username
					return -1;
				}
				
				result.next();
				return result.getInt("userID");
			}
		}

	}
	
	
	//checks the presence of user's username and password in the database
	public User checkCredentials(String username, String password) throws SQLException {
		
		String query = "SELECT userID, email, name, surname, age, city FROM tiwproject.user WHERE username = ? AND password = ?";
		
		try(PreparedStatement pstat = connection.prepareStatement(query);){
			pstat.setString(1, username);
			pstat.setString(2, password);
			try(ResultSet result = pstat.executeQuery();){
				if (!result.isBeforeFirst()) { // no results, credential check failed
					return null;
				} else {
					result.next();
					User u = new User();
					u.setID(result.getInt("userID"));
					u.setEmail(result.getString("email"));
					u.setUsername(result.getString("username"));
					u.setName(result.getString("name"));
					u.setSurname(result.getString("surname"));
					u.setPassword(result.getString("password"));
					u.setAge(result.getInt("age"));
					u.setCity(result.getString("city"));
					
					return u;
				}
			}
		}
		
	}
	
	
	//Adds a new row to the meeting table
	public int createUser(String email, String username, String name, String surname, String password, int age, String city) throws SQLException {
		int code = 0;
		
		String query = "INSERT INTO tiwproject.user (email, username, name, surname, password, age, city) VALUES(?, ?, ?, ?, ?, ?, ?)";
		
		try (PreparedStatement pstat = connection.prepareStatement(query);){
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
		

	//Checks if a username is already used or not
	public boolean checkExistence(String username) throws SQLException {
		
		String query = "SELECT userID FROM tiwproject.user WHERE username = ?";
		
		try (PreparedStatement pstat = connection.prepareStatement(query);){
			pstat.setString(1, username);
			try(ResultSet result = pstat.executeQuery();){
				if (!result.isBeforeFirst()) { // no results, username not found -> can be used
					return true;
				} else {
					result.next();
					//maybe lines 139 and 140 are useless
					User u = new User();
					u.setID(result.getInt("userID"));
					
					return false;
				}
			}
		}
		
	}
	
	
}
