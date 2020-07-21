package fr.dila.organigramme.xml.converter;

/**
 * Elément "type: valeur" contenu dans les entrées du ldif
 * 
 * @author Fabio Esposito
 * 
 */
public class Attribute {

	String	type;
	String	value;

	public Attribute(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return type + ": " + value + "\n";
	}

}
