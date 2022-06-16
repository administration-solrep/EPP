package fr.dila.st.rest.client.helper;

/**
 * Helper class for handling CDATA Strings
 * 
 * @author fbarmes
 * 
 */
public class CDataAdapter {

	private static final String	CDATA_START	= "<![CDATA[";
	private static final String	CDATA_STOP	= "]]>";

	/**
	 * Check whether a string is a CDATA string
	 * 
	 * @param s
	 *            the string to check
	 * @return
	 */
	public static boolean isCdata(String s) {
		s = s.trim();
		boolean test = (s.startsWith(CDATA_START) && s.endsWith(CDATA_STOP));
		return test;
	}

	/**
	 * Parse a CDATA String.<br/>
	 * If is a CDATA, removes leading and trailing string<br/>
	 * Otherwise does nothing
	 * 
	 * @param s
	 *            the string to parse
	 * @return the parsed string
	 */
	public static String parse(String s) {

		StringBuilder sb = null;
		s = s.trim();

		if (isCdata(s)) {

			sb = new StringBuilder(s);
			sb.replace(0, CDATA_START.length(), "");

			s = sb.toString();
			sb = new StringBuilder(s);
			sb.replace(s.lastIndexOf(CDATA_STOP), s.lastIndexOf(CDATA_STOP) + CDATA_STOP.length(), "");
			s = sb.toString();

		}

		return s;
	}

	/**
	 * Add CDATA leading and trailing to a string if not already a CDATA.
	 * 
	 * M158600: 0x2 control char is also dropped from output. It is a control character that may be added
	 * with a copy-paste of a dotted list from office documents.
	 * 
	 * @param s
	 * @return
	 */
	public static String print(String s) {
		if (isCdata(s)) {
			return s;
		} else {
			return CDATA_START + s.replace("\u0002", "") + CDATA_STOP;
		}

	}

}
