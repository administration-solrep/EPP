package fr.dila.st.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.OrganigrammeNode;

public abstract class AbstractOrganigrammeIdToLabelConverter extends AbstractConverter {

	// <type> <id>
	private static final String	errorMessageFmt	= "Impossible de retrouver le %s %s ...";
	private static final String	unkwownLabelFmt	= "***%s inconnu***";
	private final Log			log;
	private final String		type;

	/**
	 * Default constructor
	 */
	protected AbstractOrganigrammeIdToLabelConverter(Log log, final String type) {
		this.log = log;
		this.type = type;
	}

	@Override
	public final String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String id = (String) object;
			try {
				OrganigrammeNode node = getNode(id);
				if (node != null) {
					return node.getLabel();
				} else {
					log.warn(String.format(errorMessageFmt, type, id));
					return String.format(unkwownLabelFmt, type);
				}
			} catch (ClientException e) {
				log.warn(String.format(errorMessageFmt, type, id), e);
				return null;
			}
		}
		return null;
	}

	protected abstract OrganigrammeNode getNode(final String anId) throws ClientException;

}
