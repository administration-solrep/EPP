package fr.sword.naiad.commons.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * Utility fucntions for the interoperability
 * @author fbarmes
 *
 */
public class StreamUtils {

	
	/**
	 * Utility function that converts an inputString to String.
	 * Useful for converting file content to String
	 * @param is
	 * @return
	 * @throws StreamUtilsException 
	 */
	public static String inputStreamToString(InputStream is) throws StreamUtilsException  {

		if(is == null) {
			throw new StreamUtilsException("Can not convert null inputStream to String");
		}
		
		
		try {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} catch (UnsupportedEncodingException e) {
			throw new StreamUtilsException(e);
		} catch (IOException e) {
			throw new StreamUtilsException(e);
		}
	}

	
}
