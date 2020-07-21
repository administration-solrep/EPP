package fr.dila.st.webdriver.helper;

/**
 * Classe pour prendre des raccourcis de nom afin d'aider à gérer les entrées dans les boîtes d'autocomplétion
 * 
 * @author jgz
 * 
 */
public class NameShortener {

	/**
	 * Le nom complet
	 */
	private String	fullName;

	/**
	 * Le nom court qui sera entré dans la boîte de dialogue
	 */
	private String	shortName;

	/**
	 * Constructeur avec un nom court qui correspondt au nom long - 5 lettres
	 * 
	 * @param fullName
	 */
	public NameShortener(String fullName) {
		this(fullName, 5);
	}

	/**
	 * 
	 * @param fullName
	 * @param countLettersLeft
	 */
	public NameShortener(String fullName, Integer countLettersLeft) {
		this.fullName = fullName;
		this.shortName = fullName.substring(0, fullName.length() - countLettersLeft);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
}
