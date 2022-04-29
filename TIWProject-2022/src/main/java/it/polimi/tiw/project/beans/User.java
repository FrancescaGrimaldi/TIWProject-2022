package it.polimi.tiw.project.beans;

import java.io.Serializable;

/**
 * This class represents the users of the platform.
 */
public class User implements Serializable {
	private static final long serialversionUID = 1L;
	
	private int userID;
	private String name;
	private String surname;
	private String email;
	private String password;
	
	/**
	 * Class constructor.
	 */
	public User(){
	}
	
	public void setID(int id) {
		this.userID = id;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	public void setSurname(String s) {
		this.surname = s;
	}
	
	public void setEmail(String e) {
		this.email = e;
	}
	
	public void setPassword(String p) {
		this.password = p;
	}
	
	public int getID() {
		return userID;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer("User");
		
		buff.append(" id: ");
		buff.append(userID);
		buff.append(" email: ");
		buff.append(email);
		buff.append(" name: ");
		buff.append(name);
		buff.append(" surname: ");
		buff.append(surname);
		
		
		return buff.toString();
	}
}
