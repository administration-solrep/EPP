package fr.dila.st.rest.helper;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * This singleton holds and reuses the JaxB context
 * 
 * @author fbarmes
 * 
 */
public class JaxbContextHolder {

	private static JaxbContextHolder	instance;

	Map<Class<?>, JAXBContext>			contextMap;

	/**
	 * Retrieve the unique instance
	 * 
	 * @return
	 */
	public static final JaxbContextHolder getInstance() {
		if (instance == null) {
			instance = new JaxbContextHolder();
		}
		return instance;
	}

	private JaxbContextHolder() {
		super();
		this.contextMap = new HashMap<Class<?>, JAXBContext>();
	}

	/**
	 * Check wether the holder already contains a given context
	 * 
	 * @param clazz
	 * @return
	 */
	public boolean contains(Class<?> clazz) {
		return this.contextMap.containsKey(clazz);
	}

	/**
	 * Retrieve a jaxBcontext from given class type
	 * 
	 * @param clazz
	 * @return
	 * @throws JAXBException
	 */
	public JAXBContext getJaxbContext(Class<?> clazz) throws JAXBException {

		JAXBContext ctx = null;

		if (!this.contains(clazz)) {
			ctx = JAXBContext.newInstance(clazz);
			this.contextMap.put(clazz, ctx);
		}

		ctx = this.contextMap.get(clazz);
		return ctx;
	}

	/**
	 * Clear the internal JAxBContext cache
	 */
	public void clearCache() {
		this.contextMap.clear();
	}

}
