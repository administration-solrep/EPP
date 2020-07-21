package fr.dila.st.rest.client.helper;

import java.net.URL;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * Cette classe permet de fabriquer les schémas utilisés dans le socle transverse.
 * 
 * @author jtremeaux
 */
public class STSchemaFactory {
	private SchemaFactory	schemaFactory;

	private Schema			wsEvenementSchema;

	private Schema			wsEppSchema;

	/**
	 * Retourne le schéma du service WSevenement.
	 * 
	 * @return Schéma
	 * @throws SAXException
	 */
	public Schema getWsEvenementSchema() throws SAXException {
		if (wsEvenementSchema == null) {
			URL url = getClass().getResource("/xsd/solon/epp/WSevenement.xsd");
			wsEvenementSchema = getSchemaFactory().newSchema(url);
		}
		return wsEvenementSchema;
	}

	/**
	 * Retourne le schéma du service WSepp.
	 * 
	 * @return Schéma
	 * @throws SAXException
	 */
	public Schema getWsEppSchema() throws SAXException {
		if (wsEppSchema == null) {
			URL url = getClass().getResource("/xsd/solon/epp/WSepp.xsd");
			wsEppSchema = getSchemaFactory().newSchema(url);
		}
		return wsEppSchema;
	}

	protected synchronized SchemaFactory getSchemaFactory() {
		if (schemaFactory == null) {
			schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		}
		return schemaFactory;
	}
}
