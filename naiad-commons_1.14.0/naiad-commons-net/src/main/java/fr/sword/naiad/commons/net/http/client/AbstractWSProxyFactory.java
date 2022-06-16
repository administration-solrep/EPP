package fr.sword.naiad.commons.net.http.client;


/**
 * 
 * @author fbarmes
 * 
 */
public abstract class AbstractWSProxyFactory {

    private String endpoint;
    private String username;
    private String password;
    private String keyAlias;
    
    private boolean useStubs = false;

    /**
     * Default construtor. When using this, the factory will generate service stubs
     */
    public AbstractWSProxyFactory() {
        super();
        this.useStubs = true;
    }


    /**
     * Constructor for serviceProxy. When using this construtor, the factory will generate client proxy configured for a remote application with specific basePath
     * @param endpoint endpoint url to the remote server (i.e. http://hostname.domainname:8080/application-context/ws/rest)
     * @param username username for basic authentication
     * @param password password for basic authentication
     */
    public AbstractWSProxyFactory(String endpoint, String username, String password) {
    	this(endpoint, username, password, null);
    }
    
    /**
     * Constructor for serviceProxy. When using this construtor, the factory will generate client proxy configured for a remote application with specific basePath
     * 
     * @param endpoint endpoint url to the remote server (i.e. http://hostname.domainname:8080/application-context/ws/rest)
     * @param username username for basic authentication
     * @param password password for basic authentication
     * @param keyAlias the alias of the key in the keystore to use for SSL communications 
     */
    public AbstractWSProxyFactory(String endpoint, String username, String password, String keyAlias) {
        super();
        this.endpoint = endpoint;
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

    /**
     * Create a <b>stub</b> to the service
     * The stub is created according to the given service as :
     * <blockquote>
     * <pre>
     * 
     * if (clazz == DummyService.class) {
     * 	stub = (T) new DummyServiceStub();
     * }
     *
     * </pre></blockquote>
     * @param clazz the service class
     * @return a stub instance to the service
     * @throws WSProxyFactoryException is the clazz is null or the stub can not be instanciated
     */
    protected abstract <T> T getServiceStubs(Class<T> clazz) throws WSProxyFactoryException;
    
    
    /**
     * Creates an actual <b>proxy</b> to the given Service.
     * Instanciation is done as :
     * <blockquote><pre>
     * 
     * if (clazz == DummyService.class) {
     * 	proxy = new DummyServiceProxy(this.endpoint, this.basePath, this.username, this.password, this.keyAlias);
     * }
     * 
     * </pre></blockquote>
     * @param clazz
     * @return
     * @throws WSProxyFactoryException
     */
    protected abstract <T> T getServiceProxy(Class<T> clazz) throws WSProxyFactoryException;
    
    
    protected final String getEndpoint() {
        return endpoint;
    }

    protected final void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
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


	protected final String getKeyAlias() {
		return keyAlias;
	}
    
}
