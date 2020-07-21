package fr.dila.st.rest.client;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.reponses.rest.api.WSControle;
import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.reponses.rest.client.WSAttributionProxy;
import fr.dila.reponses.rest.client.WSControleProxy;
import fr.dila.reponses.rest.client.WSQuestionProxy;
import fr.dila.reponses.rest.client.WSReponseProxy;
import fr.dila.reponses.rest.stub.WSAttributionStub;
import fr.dila.reponses.rest.stub.WSControleStub;
import fr.dila.reponses.rest.stub.WSQuestionStub;
import fr.dila.reponses.rest.stub.WSReponseStub;
import fr.dila.solonepg.rest.api.WSEpg;
import fr.dila.solonepg.rest.api.WSSpe;
import fr.dila.solonepg.rest.client.WSEpgProxy;
import fr.dila.solonepg.rest.client.WSSpeProxy;
import fr.dila.solonepg.rest.stub.WSSpeStub;
import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.rest.client.WSEppProxy;
import fr.dila.solonepp.rest.client.WSEvenementProxy;
import fr.dila.solonepp.rest.stub.WSEppStub;
import fr.dila.solonepp.rest.stub.WSEvenementStub;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.stub.WSNotificationStub;

/**
 * 
 * @author fbarmes
 * @author sly
 * 
 */
public class WSProxyFactory {

	private String	endpoint;
	private String	basePath;
	private String	username;
	private String	password;
	private String	keyAlias;

	private boolean	useStubs	= false;

	/**
	 * Default construtor. When using this, the factory will generate service stubs
	 */
	public WSProxyFactory() {
		super();
		this.useStubs = true;
	}

	/**
	 * Constructor for serviceProxy. When using this construtor, the factory will generate client proxy configured for a
	 * remote application with specific basePath
	 * 
	 * @param endpoint
	 *            endpoint url to the remote server (i.e. http://hostname.domainname:8080)
	 * @param basePath
	 *            path to the root of the web service (i.e. /application-context/ws/rest)
	 * @param username
	 *            username for basic authentication
	 * @param password
	 *            password for basic authentication
	 */
	public WSProxyFactory(String endpoint, String basePath, String username, String password) {
		this(endpoint, basePath, username, password, null);
	}

	/**
	 * Constructor for serviceProxy. When using this construtor, the factory will generate client proxy configured for a
	 * remote application with specific basePath
	 * 
	 * @param endpoint
	 *            endpoint url to the remote server (i.e. http://hostname.domainname:8080)
	 * @param basePath
	 *            path to the root of the web service (i.e. /application-context/ws/rest)
	 * @param username
	 *            username for basic authentication
	 * @param password
	 *            password for basic authentication
	 * @param keyAlias
	 *            the alias of the key in the keystore to use for SSL communications
	 */
	public WSProxyFactory(String endpoint, String basePath, String username, String password, String keyAlias) {
		super();
		this.endpoint = endpoint;
		this.basePath = basePath;
		this.username = username;
		this.password = password;
		this.keyAlias = keyAlias;
		this.useStubs = false;

	}

	/**
	 * Return a service proxy or stub according to which constructor has been used
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public final <T> T getService(Class<T> clazz) throws WSProxyFactoryException {

		if (this.useStubs) {
			return getServiceStubs(clazz);
		} else {
			return getServiceProxy(clazz);
		}
	}

	@SuppressWarnings("unchecked")
	private final <T> T getServiceStubs(Class<T> clazz) throws WSProxyFactoryException {

		T stub = null;

		if (clazz == null) {
			throw new WSProxyFactoryException("Can not create instance of null");
		}

		if (clazz == WSAttribution.class) {
			stub = (T) new WSAttributionStub();

		} else if (clazz == WSControle.class) {
			stub = (T) new WSControleStub();

		} else if (clazz == WSNotification.class) {
			stub = (T) new WSNotificationStub();

		} else if (clazz == WSQuestion.class) {
			stub = (T) new WSQuestionStub();

		} else if (clazz == WSReponse.class) {
			stub = (T) new WSReponseStub();
		} else if (clazz == WSEpp.class) {
			stub = (T) new WSEppStub();
		} else if (clazz == WSSpeStub.class) {
			stub = (T) new WSSpeStub();
		} else if (clazz == WSEvenement.class) {
			stub = (T) new WSEvenementStub();
		} else {
			throw new WSProxyFactoryException("Can not create instance of " + clazz.getName()
					+ " : unsupported interface");
		}

		return stub;
	}

	@SuppressWarnings("unchecked")
	private final <T> T getServiceProxy(Class<T> clazz) throws WSProxyFactoryException {

		AbstractWsProxy proxy = null;

		if (clazz == null) {
			throw new WSProxyFactoryException("Can not create instance of null");
		}

		if (clazz == WSAttribution.class) {
			proxy = new WSAttributionProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSControle.class) {
			proxy = new WSControleProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSNotification.class) {
			proxy = new WSNotificationProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSQuestion.class) {
			proxy = new WSQuestionProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSReponse.class) {
			proxy = new WSReponseProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSEpp.class) {
			proxy = new WSEppProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSSpe.class) {
			proxy = new WSSpeProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == WSEvenement.class) {
			proxy = new WSEvenementProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else if (clazz == fr.dila.solonepp.rest.api.WSNotification.class) {
			proxy = new fr.dila.solonepp.rest.client.WSNotificationProxy(this.endpoint, this.basePath, this.username,
					this.password, this.keyAlias);
		} else if (clazz == WSEpg.class) {
			proxy = new WSEpgProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
		} else {
			throw new WSProxyFactoryException("Can not create instance of " + clazz.getName()
					+ " : unsupported interface");
		}

		return (T) proxy;
	}

	protected final String getEndpoint() {
		return endpoint;
	}

	protected final void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	protected final String getBasePath() {
		return basePath;
	}

	protected final void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	protected final String getUsername() {
		return username;
	}

	protected final void setUsername(String username) {
		this.username = username;
	}

	protected final String getPassword() {
		return password;
	}

	protected final void setPassword(String password) {
		this.password = password;
	}

	protected final boolean isUseStubs() {
		return useStubs;
	}

	protected final void setUseStubs(boolean useStubs) {
		this.useStubs = useStubs;
	}

}
