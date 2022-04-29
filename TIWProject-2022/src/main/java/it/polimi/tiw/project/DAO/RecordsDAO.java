package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.User;

public class RecordsDAO {
	private Connection connection;
	
	public RecordsDAO(Connection conn) {
		this.connection = conn;
	}
	
	public List<User> findAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		
		try(PreparedStatement pstat = connection.prepareStatement("SELECT * FROM tiwproject.user");){
			try (ResultSet result = pstat.executeQuery();) {
				while (result.next()) {
					User u = new User();
					u.setID(result.getInt("userID"));
					u.setName(result.getString("name"));
					u.setSurname(result.getString("surname"));
					u.setPassword(result.getString("password"));
					users.add(u);
				}
			}
		}
		return users;
	}
				
		
	}
}
