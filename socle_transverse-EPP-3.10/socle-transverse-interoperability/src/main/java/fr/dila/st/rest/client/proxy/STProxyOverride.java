package fr.dila.st.rest.client.proxy;

import java.net.Proxy;
import java.net.ProxySelector;
import java.util.List;

public class STProxyOverride {

	private final List<String> hostnames;

	private final Proxy customProxy;

	private ProxySelector originalProxySelector;

	public STProxyOverride(List<String> hostnames, Proxy customProxy) {
		super();
		this.hostnames = hostnames;
		this.customProxy = customProxy;
	}

	public void replaceProxySelector() {
		// récupération du ProxySelector pour remplacement par un STProxySelector
		originalProxySelector = ProxySelector.getDefault();
		STProxySelector stProxySelector = new STProxySelector(originalProxySelector, hostnames, customProxy);
		ProxySelector.setDefault(stProxySelector);
	}

	public void resetProxySelector() {
		ProxySelector.setDefault(originalProxySelector);
	}

}
