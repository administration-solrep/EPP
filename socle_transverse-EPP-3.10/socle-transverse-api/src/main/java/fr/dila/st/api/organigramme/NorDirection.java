package fr.dila.st.api.organigramme;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

/**
 * Contient un id de ministere et la lettre nor correspondante Permet de sérialiser le tout dans une chaine
 * 
 * @author Fabio Esposito
 * 
 */
public class NorDirection {

	/**
	 * loggeur
	 */
	private static final Log	log	= LogFactory.getLog(NorDirection.class);

	private String				id;

	private String				nor;

	public static final String		KEY_ID	= "ID";

	public static final String		KEY_NOR	= "NOR";

	public NorDirection(String norRefJSON) {
		Map<String, String> norMap = unSerializeNOR(norRefJSON);
		if (norMap != null) {
			id = norMap.get(KEY_ID);
			nor = norMap.get(KEY_NOR);
		}
	}

	public NorDirection(String ident, String nor) {
		this.id = ident;
		this.nor = nor;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the nor
	 */
	public String getNor() {
		return nor;
	}

	/**
	 * @param nor
	 *            the nor to set
	 */
	public void setNor(String nor) {
		this.nor = nor;
	}

	public String getNorJSON() {
		return serializeNOR(id, nor);
	}

	private String serializeNOR(String parentId, String norRef) {

		String json = null;
		try {
			json = new JSONStringer().object().key(KEY_ID).value(parentId).key(KEY_NOR).value(norRef).endObject().toString();
		} catch (JSONException e) {
			log.error("Error generating JSON", e);
		}
		return json;
	}

	private Map<String, String> unSerializeNOR(String norJSON) {

		Map<String, String> norRef = new HashMap<String, String>();
		try {
			JSONTokener tokener = new JSONTokener(norJSON);
			Object obj = tokener.nextValue();
			if (obj instanceof JSONObject) {
				JSONObject object = (JSONObject) obj;
				String ident = object.getString(KEY_ID);
				String lNor = object.getString(KEY_NOR);
				norRef.put(KEY_ID, ident);
				norRef.put(KEY_NOR, lNor);
			} else {
				return null;
			}
		} catch (JSONException e) {
			log.error("Error getting JSON value", e);
		}
		return norRef;
	}
}
