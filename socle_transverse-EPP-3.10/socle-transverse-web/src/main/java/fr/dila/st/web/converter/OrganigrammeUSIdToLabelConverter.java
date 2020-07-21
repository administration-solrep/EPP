package fr.dila.st.web.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Converter JSF qui fournit le label d'une unité structurelle.
 * 
 * @author Fabio Esposito
 */
public class OrganigrammeUSIdToLabelConverter extends AbstractOrganigrammeIdToLabelConverter {

	private static final Log	LOG	= LogFactory.getLog(OrganigrammeUSIdToLabelConverter.class);

	/**
	 * Default constructor
	 */
	public OrganigrammeUSIdToLabelConverter() {
		super(LOG, "unité structurelle");
	}

	@Override
	protected OrganigrammeNode getNode(final String anId) throws ClientException {
		return STServiceLocator.getSTUsAndDirectionService().getUniteStructurelleNode(anId);
	}

}