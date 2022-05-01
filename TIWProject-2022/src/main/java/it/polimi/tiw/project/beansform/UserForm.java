package it.polimi.tiw.project.beansform;

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
	private String emailError;
	private String usernameError;
	private String passwordError;
	private String nameError;
	private String surnameError;
	private String ageError;
	private String cityError;
	
	
	public UserForm() {
		super();
	}

	
	public UserForm(String email, String username, String password, String password2, String name,
			String surname, int age, String city) {
		super();
		this.setEmail(email);
		this.setUsername(username);
		this.setPassword(password,password2);
		this.setName(name);
		this.setSurname(surname);
		this.setAge(age);
		this.setCity(city);
	}
	
	
	public String getEmail() {
		return email;
	}
	
	
	public void setEmail(String email) {
		this.email = email;
		if (email == null || email.isEmpty()) {
			this.emailError = "You didn't enter an email!";
		} else if (email.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$") {
			this.emailError = null;
		} else {
			this.emailError = "Email format is example@mail.com";
		}
	}
	
	
	public String getUsername() {
		return username;
	}
	
	
	public void setUsername(String username) {
		//TODO:check (through UserDAO) if the selected username doesn't match one already in the database
	}
	
	
	public String getPassword() {
		return password;
	}
	
	
	public void setPassword(String password, String password2) {
		this.password = password;
		
		if ( !(password == null || password.isEmpty()) ) {
			
			if(password.equals(password2)) {
				this.passwordError = null;
			} else {
				this.passwordError = "Passwords don't match";
			}
			
		} else {
			this.passwordError = "Password field is empty";
		}
		
	}

	
	public String getName() {
		return name;
	}
	
	
	public void setName(String name) {
		this.name = name;
		
		if(name.matches("[a-zA-Z]+")) {
			this.nameError = null;
		} else {
			this.nameError = "Name field can only contain letters";
		}
		
	}
	
	
	public String getSurname() {
		return surname;
	}
	
	
	public void setSurname(String surname) {
		this.surname = surname;
			
		if(surname.matches("[a-zA-Z]+")) {
			this.surnameError = null;
		} else {
			this.surnameError = "Surname field can only contain letters";
		}
		
	}
	
	
	public int getAge() {
		return age;
	}
	
	
	public void setAge(int age) {
		this.age = age;
		
		if(age < 5 || age > 123) {
			this.ageError = "Please enter a valid age";
		} else {
			this.ageError = null;
		}
		
	}
	
	
	public String getCity() {
		return city;
	}
	
	
	public void setCity(String city) {
		this.city = city;
		
		if(city.matches("[a-zA-Z]+")) {
			this.cityError = null;
		} else {
			this.cityError = "City field can only contain letters";
		}
		
	}
	

	public String getEmailError() {
		return emailError;
	}
	
	
	public String getUsernameError() {
		return usernameError;
	}
	
	
	public String getPasswordError() {
		return passwordError;
	}
	
	
	public String getNameError() {
		return nameError;
	}
	
	
	public String getSurnameError() {
		return surnameError;
	}
	
	
	public String getAgeError() {
		return ageError;
	}
	
	
	public String getCityError() {
		return cityError;
	}
	
	
	public boolean isValid() {
		return this.emailError == null && this.usernameError == null && this.passwordError == null
				&& this.nameError == null && this.surnameError == null && this.ageError == null
				&& this.cityError == null;
	}
}
