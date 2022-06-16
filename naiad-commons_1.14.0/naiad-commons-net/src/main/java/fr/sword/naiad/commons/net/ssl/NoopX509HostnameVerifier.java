package fr.sword.naiad.commons.net.ssl;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.http.conn.ssl.X509HostnameVerifier;

/**
 * A Hostname verifier that does nothing
 * @author fbarmes
 *
 */
public class NoopX509HostnameVerifier implements X509HostnameVerifier {
	
	public NoopX509HostnameVerifier() {
		super();
	}
	
	/**
	 * always true
	 */
	public boolean verify(String arg0, SSLSession arg1) {
		return true;
	}
	
	/**
	 * does nothing
	 */
	public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {
		//--- voluntarily doing nothing
	}
	
	/**
	 * does nothing
	 */
	public void verify(String arg0, X509Certificate arg1) throws SSLException {
		//--- voluntarily doing nothing
	}
	
	/**
	 * does nothing
	 */
	public void verify(String arg0, SSLSocket arg1) throws IOException {
		//--- voluntarily doing nothing
	}
}

