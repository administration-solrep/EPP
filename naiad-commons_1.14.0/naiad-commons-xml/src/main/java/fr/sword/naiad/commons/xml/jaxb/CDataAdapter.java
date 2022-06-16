package fr.sword.naiad.commons.xml.jaxb;


/**
 * Helper class for handling CDATA Strings
 * @author fbarmes
 *
 */
public final class CDataAdapter  {


	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_STOP = "]]>";
	
	/**
	 * Helper private constructor
	 */
	private CDataAdapter() {
		super();
	}
	
	/**
	 * Check whether a string is a CDATA string
	 * @param inStr the string to check
	 * @return
	 */
	public static boolean isCdata(String inStr) {
		String str = inStr.trim();
		return str.startsWith(CDATA_START) && str.endsWith(CDATA_STOP);
	}
	
	/**
	 * Parse a CDATA String.<br/>
	 * If is a CDATA, removes leading and trailing string &lt;br/&gt;
	 * Otherwise does nothing
	 * @param inStr the string to parse
	 * @return the parsed string
	 */
	public static String parse(String inStr)  {
		
		StringBuilder stringBuilder = null;
		String str = inStr.trim();
		
		if(isCdata(str)) {
			
			
			stringBuilder = new StringBuilder(str);
			stringBuilder.replace(0, CDATA_START.length(), "");
			
			str = stringBuilder.toString();
			stringBuilder = new StringBuilder(str);			
			stringBuilder.replace(str.lastIndexOf(CDATA_STOP), str.lastIndexOf(CDATA_STOP)+CDATA_STOP.length(), "");
			str = stringBuilder.toString();
			
		}
		
		return str;
	}
	
	/**
	 * Add CDATA leading and trailing to a string if not already a CDATA
	 * @param str
	 * @return
	 */
	public static String print(String str) {
		
		if(isCdata(str)) {
			return str;
		} else {
			return CDATA_START + str + CDATA_STOP;
		}
	}
	
}
