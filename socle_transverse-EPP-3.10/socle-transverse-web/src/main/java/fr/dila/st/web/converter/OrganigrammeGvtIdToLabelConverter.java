package fr.dila.st.web.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Converter JSF qui fournit le label d'un gouvernement
 * 
 * @author Fabio Esposito
 */
public class OrganigrammeGvtIdToLabelConverter extends AbstractOrganigrammeIdToLabelConverter {

	private static final Log	LOG	= LogFactory.getLog(OrganigrammeGvtIdToLabelConverter.class);

	public OrganigrammeGvtIdToLabelConverter() {
		super(LOG, "gouvernement");
	}

	@Override
	protected OrganigrammeNode getNode(final String anId) throws ClientException {

		return STServiceLocator.getSTGouvernementService().getGouvernement(anId);

	}

}
