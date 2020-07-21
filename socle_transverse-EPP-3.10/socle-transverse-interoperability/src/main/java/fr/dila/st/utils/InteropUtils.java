package fr.dila.st.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import fr.dila.dictao.d2s.proxy.D2SServiceCallerException;

/**
 * Utility fucntions for the interoperability
 * 
 * @author fbarmes
 * 
 */
public class InteropUtils {

	/**
	 * Utility function that converts an inputString to String. Useful for converting file content to String
	 * 
	 * @param is
	 * @return
	 * @throws InteropUtilsException
	 * @throws D2SServiceCallerException
	 */
	public static String inputStreamToString(InputStream is) throws InteropUtilsException {

		if (is == null) {
			throw new InteropUtilsException("Can not convert null inputStream to String");
		}

		try {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} catch (UnsupportedEncodingException e) {
			throw new InteropUtilsException(e);
		} catch (IOException e) {
			throw new InteropUtilsException(e);
		}
	}

}
