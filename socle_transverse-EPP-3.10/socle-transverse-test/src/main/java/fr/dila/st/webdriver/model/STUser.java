package fr.dila.st.webdriver.model;

/**
 * Un modèle simple pour un utilisateur du système solon
 * 
 * @author jgomez
 * 
 */
public class STUser {
	private String	login;
	private String	password;
	private String	poste;

	public STUser(String login, String password, String poste) {
		super();
		this.login = login;
		this.password = password;
		this.poste = poste;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPoste() {
		return poste;
	}

	public void setPoste(String poste) {
		this.poste = poste;
	}
}
