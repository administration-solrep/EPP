package fr.sword.naiad.nuxeo.commons.core.model;

/**
 * Bean pour stocker les données d'un utilisateur
 * 
 * @author SPL
 *
 */
public class UserModel {
	
	private String userName;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

	public UserModel() {
		super();
	}

	public UserModel(String userName, String firstName, String lastName) {
		this(userName, firstName, lastName, null, null);
	}

	public UserModel(String userName, String firstName, String lastName, String email, String phone) {
		super();
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		if(firstName == null && lastName == null){
			return userName;
		} else {
			return firstName + " " + lastName;
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
