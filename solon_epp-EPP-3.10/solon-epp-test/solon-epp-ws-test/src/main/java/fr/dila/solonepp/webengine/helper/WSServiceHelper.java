package fr.dila.solonepp.webengine.helper;

import junit.framework.Assert;
import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.constant.URLConstant;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.client.WSProxyFactoryException;

/**
 * 
 * 
 * 
 * @author SPL
 * 
 */
public final class WSServiceHelper {

	public static final String GVT_LOGIN = "ws-gouvernement";
	public static final String GVT_PASSWD = GVT_LOGIN;
	
	public static final String AN_LOGIN = "ws-an";
	public static final String AN_PASSWD = AN_LOGIN;
	
	public static final String SENAT_LOGIN = "ws-senat";
	public static final String SENAT_PASSWD = SENAT_LOGIN;
	
	
	public static WSEpp getWSEpp(String login, String password) throws  WSProxyFactoryException {
		WSProxyFactory proxyFactory = new WSProxyFactory(UrlHelper.getEndPoint(), URLConstant.BASE_PATH, login, password, null);
		Assert.assertNotNull(proxyFactory);

		WSEpp wsEpp = proxyFactory.getService(WSEpp.class);
		Assert.assertNotNull(wsEpp);

		return wsEpp;
	}

	public static WSEvenement getWSEvenement(String login, String password) throws WSProxyFactoryException {
        WSProxyFactory proxyFactory = new WSProxyFactory(UrlHelper.getEndPoint(), URLConstant.BASE_PATH, login, password, null);
        Assert.assertNotNull(proxyFactory);
        
        WSEvenement wsEvenement = proxyFactory.getService(WSEvenement.class);
        Assert.assertNotNull(wsEvenement);
        
        return wsEvenement;
    }
	
	public static WSEvenement getWSEvenementGvt()  throws WSProxyFactoryException {
		return getWSEvenement(GVT_LOGIN, GVT_PASSWD);
	}
	
	public static WSEvenement getWSEvenementAn()  throws WSProxyFactoryException {
		return getWSEvenement(AN_LOGIN, AN_PASSWD);
	}
	
	public static WSEvenement getWSEvenementSenat()  throws WSProxyFactoryException {
		return getWSEvenement(SENAT_LOGIN, SENAT_PASSWD);
	}
	
	public static WSEpp getWSEppGvt() throws WSProxyFactoryException{
		return getWSEpp(GVT_LOGIN, GVT_PASSWD);
	}
	
	public static WSEpp getWSEppAn() throws WSProxyFactoryException{
		return getWSEpp(AN_LOGIN, AN_PASSWD);
	}
	
	public static WSEpp getWSEppSenat() throws WSProxyFactoryException{
		return getWSEpp(SENAT_LOGIN, SENAT_PASSWD);
	}
	
	/**
	 * utility class
	 */
	private WSServiceHelper() {
		// do nothing
	}

}
