package it.polimi.tiw.project.beans;

/**
 * This class represents the users of the platform.
 */
public class User {
	
	private int userID;
	private String username;
	private String name;
	private String surname;
	private String email;
	private String password;
	private int age;
	private String city;
	
	
	/**
	 * Class constructor.
	 */
	public User(){
	}
	
	
	/* The following methods are setters for this class' attributes */
	
	public void setID(int id) {
		this.userID = id;
	}
	
	public void setUsername(String nick) {
		this.username = nick;
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
	
	public void setAge(int a) {
		this.age = a;
	}
	
	public void setCity(String c) {
		this.city = c;
	}
	
	
	/* The following methods are getters for this class' attributes */
	
	public int getID() {
		return userID;
	}
	
	public String getUsername() {
		return username;
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
	
	public int getAge() {
		return age;
	}
	
	public String getCity() {
		return city;
	}
	
	public String toString() {
		StringBuffer buff = new StringBuffer("User");
		
		buff.append(" id: ");
		buff.append(userID);
		buff.append(" username: ");
		buff.append(username);
		buff.append(" email: ");
		buff.append(email);
		buff.append(" name: ");
		buff.append(name);
		buff.append(" surname: ");
		buff.append(surname);
		buff.append(" age: ");
		buff.append(age);
		buff.append(" lives in: ");
		buff.append(city);
		
		return buff.toString();
	}

}