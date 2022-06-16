package fr.sword.naiad.nuxeo.commons.core.util;

/**
 * Class utilitaire : Manipulation des constantes sur les schemas
 * 
 * @author SPL
 *
 */
public final class SchemaUtil {
	
	/**
	 * Class utilitaire : pas de constructeur
	 */
	private SchemaUtil(){
		
	}
	
	/**
	 * Concatene le prefix d'un schema et une propriété pour obtenir
	 * la valeur utilisable dans les requetes NXQL
	 * @param prefix prefixe du schema
	 * @param property propriété du schema
	 * @return la chaine de caractere "prefix:property"
	 */
	public static String xpath(String prefix, String property){
		if(prefix == null || prefix.isEmpty()){
		  return property;	
		} else {
		  return prefix + ':' + property;
		}
	}
	
}
