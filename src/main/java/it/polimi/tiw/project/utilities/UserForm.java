package it.polimi.tiw.project.utilities;

/**
* This class provides methods to check if the input inserted in 
* the createUser form is correct and displays potential errors.
* Note that the parameters name, surname, age and city were not
* in the specifications. 
*/
public class UserForm {
	private String email;
	private String username;
	private String password;
	private String name;
	private String surname;
	private int age;
	private String city;
	private String emailError = null;
	private String usernameError = null;
	private String passwordError = null;
	private String nameError = null;
	private String surnameError = null;
	private String ageError = null;
	private String cityError = null;
	
	
	/**
	 * Default constructor.
	 */
	public UserForm() {
		super();
	}

	/**
	 * Class constructor specifying the parameters got from user input.
	 * @param email		the String containing the email.
	 * @param username	the String containing the username.
	 * @param password	the String containing the password.
	 * @param password2	the String containing the password repeated (-> needs to match "password").
	 * @param name		the String containing the name of the user.
	 * @param surname	the String containing the surname of the user.
	 * @param age		the age of the user.
	 * @param city		the String containing the city of the user.
	 */
	public UserForm(String email, String username, String password, String password2, String name,
			String surname, Integer age, String city) {
		setEmail(email);
		setUsername(username);
		setPassword(password,password2);
		setName(name);
		setSurname(surname);
		setAge(age);
		setCity(city);
	}
	
	
	/**
	 * Sets the email of the user checking that it's not empty and that
	 * the format is valid (using a regex).
	 * @param email		the email inserted.
	 */
	public void setEmail(String email) {
		this.email = email;
		
		if (email == null || email.equals("")) {
			this.emailError = "Email field cannot be empty.";
		} else if ( !(email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")) ) {
			this.emailError = "Email format is example@mail.com.";
		} else {
			this.emailError = null;
		}
	}
	
	
	/**
	 * Sets the username of the user checking that it's not empty and that
	 * the format is valid (using a regex).
	 * @param username	the username inserted.
	 */
	public void setUsername(String username) {
		//NOTE: the availability of the username is checked by a CreateUser method (checkUsername) 
		//that calls UserDAO's method checkExistence
		
		this.username = username;
		
		if (username == null || username.equals("")) {
			this.usernameError = "Username field cannot be empty.";
		} else if ( !(username.matches("[a-zA-Z0-9]+")) ) {
			this.usernameError = "Username can only contain letters and numbers.";
		} else {
			this.usernameError = null;
		}
	}
	
	
	/**
	 * Sets the password of the user checking that it's not empty and that
	 * the two password fields match.
	 * @param password	the password inserted.
	 * @param password2	the password repeated by the user.
	 */
	public void setPassword(String password, String password2) {
		this.password = password;

		if (password == null || password.equals("") || password2 == null || password2.equals("")) {
			this.passwordError = "Password field cannot be empty.";
		} else if ( !(password.equals(password2)) ) {
			this.passwordError = "Passwords have to match.";
		} else {
			this.passwordError = null;
		}
	}

	
	/**
	 * Sets the name of the user checking that it's not empty and that
	 * it only contains letters (using a regex).
	 * @param name		the name inserted.
	 */
	public void setName(String name) {
		this.name = name;
		
		if(name == null || name.equals("")) {
			this.nameError = "Name field cannot be empty.";
		} else if( !(name.matches("[a-zA-Z]+")) ) {
			this.nameError = "Name field can only contain letters.";
		} else {
			this.nameError = null;
		}
	}
	
	
	/**
	 * Sets the surname of the user checking that it's not empty and that
	 * it only contains letters (using a regex).
	 * @param surname	the surname inserted.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
		
		if(surname == null || surname.equals("")) {
			this.surnameError = "Surname field cannot be empty.";
		} else if( !(surname.matches("[a-zA-Z]+")) ) {
			this.surnameError = "Surname field can only contain letters.";
		} else {
			this.surnameError = null;
		}
	}
	
	
	/**
	 * Sets the age of the user checking that it's valid.
	 * @param age		the age inserted.
	 */
	public void setAge(Integer age) {
		this.age = age;
		
		if(age < 5 || age > 123) {
			this.ageError = "The age inserted must be valid.";
		} else {
			this.ageError = null;
		}
	}
	
	
	/**
	 * Sets the city of the user checking that it's not empty and that
	 * it only contains letters (using a regex).
	 * @param city		the city inserted.
	 */
	public void setCity(String city) {
		this.city = city;
		
		if(city == null || city.equals("")) {
			this.cityError = "City field cannot be empty.";
		} else if ( !(city.matches("[a-zA-Z]+")) ) {
			this.cityError = "City field can only contain letters.";
		} else {
			this.cityError = null;
		}
	}
	

	/* The following methods are getters for this class' attributes */
	
	public String getEmail() {
		return email;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public int getAge() {
		return age;
	}
		
	public String getCity() {
		return city;
	}
	
	
	/* The following methods are getters for the error strings */
	
	public String getErrors(){
		String error = " ";

		if(usernameError!=null) error=error.concat(usernameError+" ");
		if(emailError!=null) error=error.concat(emailError+" ");
		if(passwordError!=null) error=error.concat(passwordError+" ");
		if(nameError!=null) error=error.concat(nameError+" ");
		if(surnameError!=null) error=error.concat(surnameError+" ");
		if(ageError!=null) error=error.concat(ageError+" ");
		if(cityError!=null) error=error.concat(cityError+" ");

		return error;
	}
	
	
	/**
	 * Checks the validity of the user input for the account creation.
	 * If all the error strings == null -> there are no errors -> the form is
	 * valid -> the user can proceed with the creation of the meeting, selecting
	 * the participants.
	 * @return			a boolean whose value is:
	 * 					<p>
	 * 					-{@code true} if all the input is valid;
	 * 					</p> <p>
	 * 					-{@code false} otherwise.
	 * 					</p>
	 */
	public boolean isValid() {
		return ( (emailError == null) && (usernameError == null) && (passwordError == null)
				&& (nameError == null) && (surnameError == null) && (ageError == null)
				&& (cityError == null) );
	}

}