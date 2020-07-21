package fr.dila.st.core.query.translation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Une classe pour représenter les informations des instructions NXSQL traduites.
 * 
 * @author jgomez
 * 
 */

public class TranslatedStatement {

	private String				logicalOperator;
	private String				searchField;
	private String				conditionalOperator;
	private String				value;
	protected List<Converter>	converters	= Arrays.asList(new Converter[] { new DateConverter(), new ListConverter() });

	public TranslatedStatement(String logicalOperator, String searchField, String conditionalOperator, String value) {
		super();
		this.logicalOperator = logicalOperator;
		this.searchField = searchField;
		this.conditionalOperator = conditionalOperator;
		this.value = value;
	}

	public TranslatedStatement() {
		super();
	}

	public String getLogicalOperator() {
		if (StringUtils.isBlank(logicalOperator)) {
			return StringUtils.EMPTY;
		}
		return logicalOperator;
	}

	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getConditionalOperator() {
		if (StringUtils.isBlank(conditionalOperator)) {
			return StringUtils.EMPTY;
		}
		return conditionalOperator;
	}

	public void setConditionalOperator(String conditionalOperator) {
		this.conditionalOperator = conditionalOperator;
	}

	public String getValue() {
		if (value == null) {
			return "";
		}
		return value;
	}

	/**
	 * Retourne une valeur directement compréhensible par l'utilisateur, donc sans les guillemets simples au début et à
	 * la fin
	 * 
	 * @return
	 */
	public String getUserFriendlyValue() {
		String value = getValue();
		if (value.startsWith("'") && value.endsWith("'")) {
			return value.substring(1, value.length() - 1).replace("','", ",");
		}
		return value.replace("'", "");
	}

	/**
	 * Retourne une valeur technique, c'est à dire une valeur valable pour le UFNXQL, donc sans les guillemets simples/
	 * 
	 * @return
	 */
	public String getTechnicalValue() {
		String value = getValue();
		for (Converter converter : this.converters) {
			String convertedValue = converter.convert(this, value);
			if (!value.equals(convertedValue)) {
				return convertedValue;
			}
		}
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString(Boolean technicalUse) {
		String result = "";
		String value = technicalUse ? getTechnicalValue() : getValue().replace("'", "");
		result = searchField + " " + conditionalOperator + " " + value;
		if (!StringUtils.isEmpty(logicalOperator)) {
			result += " " + logicalOperator;
		}
		return result;
	}

	public String toString() {
		return toString(false);
	}

	/**
	 * 
	 * Une interface pour gérer les converteurs de valeur utilisateurs vers le language utilisateur de manière uniforme
	 * 
	 * @author jgomez
	 * 
	 */

	public interface Converter {
		/**
		 * Retourne la valeur convertie, si il n'y a rien faire, on laisse la valeur inchangée.
		 * 
		 * @param input
		 * @return la chaîne convertie.
		 */
		String convert(TranslatedStatement t, String input);
	}

	/**
	 * 
	 * Un convertisseur pour les listes de valeurs.
	 * 
	 * @author jgomez
	 * 
	 */
	public static class ListConverter implements Converter {

		private static final String	IN	= "IN";

		/**
		 * Default constructor
		 */
		public ListConverter() {
			// do nothing
		}

		public String convert(TranslatedStatement t, String userInput) {
			if (IN.equals(t.getConditionalOperator())) {
				// String result = StringUtils.replace(userInput," ","");
				return "(" + userInput + ")";
			} else {
				return userInput;
			}
		}
	}

	/**
	 * 
	 * Un convertisseur pour les intervalles de date.
	 * 
	 * @author jgomez
	 * 
	 */

	public static class DateConverter implements Converter {
		private static final String	BETWEEN	= "BETWEEN";

		/**
		 * Default constructor
		 */
		public DateConverter() {
			// do nothing
		}

		public String convert(TranslatedStatement t, String userInput) {
			if (userInput.contains("DATE") || BETWEEN.equals(t.getConditionalOperator())) {
				String result = StringUtils.replace(userInput, ",", " AND ");
				// Il faut mettre les guillements ismplaes autours des datess
				Pattern datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");
				result = datePattern.matcher(result).replaceAll("$0");
				return result;
			} else {
				return userInput;
			}
		}
	}

}
