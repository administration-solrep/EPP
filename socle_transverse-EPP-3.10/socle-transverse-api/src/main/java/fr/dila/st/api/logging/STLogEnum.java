package fr.dila.st.api.logging;

import fr.dila.st.api.logging.enumerationCodes.STObjetsEnum;
import fr.dila.st.api.logging.enumerationCodes.STPorteesEnum;
import fr.dila.st.api.logging.enumerationCodes.STTypesEnum;

/**
 * Interface des énumérations de log codifiés
 * 
 * Code : 000_000_000 => [{@link STTypesEnum}]_[{@link STPorteesEnum}]_[{@link STObjetsEnum}]
 * 
 */
public interface STLogEnum {

	/**
	 * Séparateur des codes enumerations
	 */
	static final String	SEPARATOR_CODE	= "_";

	/**
	 * compose le code du log sous la forme 000_000_000
	 * 
	 * @return le code composé du type log, portee, et objet de log
	 */
	String getCode();

	/**
	 * @return le texte du log
	 */
	String getText();

	/**
	 * compose la ligne de log composée du code et du texte de log <br />
	 * format : "[CODE:000_000_000]Texte"
	 * 
	 * @return
	 */
	@Override
	String toString();

}