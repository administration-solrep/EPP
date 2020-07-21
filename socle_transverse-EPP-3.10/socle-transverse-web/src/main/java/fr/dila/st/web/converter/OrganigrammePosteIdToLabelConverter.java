package fr.dila.st.web.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Converter JSF qui fournit le label d'un poste.
 * 
 * @author Fabio Esposito
 */
public class OrganigrammePosteIdToLabelConverter extends AbstractOrganigrammeIdToLabelConverter {

	private static final Log	LOG	= LogFactory.getLog(OrganigrammePosteIdToLabelConverter.class);

	/**
	 * Default constructor
	 */
	public OrganigrammePosteIdToLabelConverter() {
		super(LOG, "poste");
	}

	@Override
	protected OrganigrammeNode getNode(final String anId) throws ClientException {
		return STServiceLocator.getSTPostesService().getPoste(anId);
	}

}
