package fr.sword.naiad.commons.net.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;


/**
 * A TrustManager that trusts all
 * @author fbarmes
 *
 */
public class NoopTrustManager implements X509TrustManager {
	
	public NoopTrustManager() {
		super();
	}
	
	/**
	 * does nothing
	 */
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		//--- voluntarily doing nothing
	}
	
	/**
	 * does nothing
	 */
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		//--- voluntarily doing nothing
	}
	
	//@Override
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[]{};
	}		
}
