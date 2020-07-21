package fr.dila.st.rest.client.proxy;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class STProxySelector extends ProxySelector {

	private static final Logger LOGGER = Logger.getLogger(STProxySelector.class);

	private final ProxySelector originalProxySelector;

	private final List<String> hostnames;

	private final List<Proxy> proxyList;

	STProxySelector(ProxySelector originalProxySelector, List<String> hostnames, Proxy customProxy) {
		this.originalProxySelector = originalProxySelector;
		this.hostnames = hostnames;
		this.proxyList = new ArrayList<Proxy>();
		this.proxyList.add(customProxy);
	}

	@Override
	public List<Proxy> select(URI uri) {
		// switch entre le proxy custom ou le sélecteur original
		if (startsWithPrefix(uri)) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("Using custom logger for %s", uri.toString()));
			}
			return proxyList;
		} else {
			return originalProxySelector.select(uri);
		}
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		if (startsWithPrefix(uri)) {
			throw new RuntimeException("Failure to contact custom proxy", ioe);
		} else {
			originalProxySelector.connectFailed(uri, sa, ioe);
		}
	}

	private boolean startsWithPrefix(URI uri) {
		for (String hostname : hostnames) {
			if (uri.getHost().toLowerCase().startsWith(hostname.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

}
