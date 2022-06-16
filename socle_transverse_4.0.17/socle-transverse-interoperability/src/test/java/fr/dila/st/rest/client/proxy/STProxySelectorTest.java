package fr.dila.st.rest.client.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.Assert;

@Ignore
public class STProxySelectorTest {

	// Ces tests nécessitent la configuration d'un proxy en local

	@Test
	public void test01() {
		try {
			// Create the request
			URL url = new URL("http://google.com");
			HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// Execute the request
			int status = con.getResponseCode();
			if (status != 200) {
				Assert.fail();
			}
			// Disconnect
			con.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void test02() {
		try {
			// Turn on proxy
			List<String> urlPrefixes = new ArrayList<String>();
			urlPrefixes.add("google");
			urlPrefixes.add("www.google");
			Proxy customProxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 3128));
			STProxyOverride stProxyOverride = new STProxyOverride(urlPrefixes, customProxy);
			stProxyOverride.replaceProxySelector();
			// Create the request
			URL url = new URL("http://google.com");
			HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// Execute the request
			int status = con.getResponseCode();
			if (status != 200) {
				Assert.fail();
			}
			// Disconnect
			con.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void test03() {
		try {
			// Turn on proxy
			List<String> urlPrefixes = new ArrayList<String>();
			urlPrefixes.add("google");
			urlPrefixes.add("www.google");
			Proxy customProxy = new Proxy(Type.SOCKS, new InetSocketAddress("localhost", 3129));
			STProxyOverride stProxyOverride = new STProxyOverride(urlPrefixes, customProxy);
			stProxyOverride.replaceProxySelector();
			// Create the request
			URL url = new URL("http://google.com");
			HttpURLConnection con;
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			// Execute the request
			int status = con.getResponseCode();
			if (status != 200) {
				Assert.fail();
			}
			// Disconnect
			con.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
