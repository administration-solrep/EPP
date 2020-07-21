package fr.dila.solonepp.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.service.OrganigrammeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SessionUtil;

/**
 * Converter JSF qui fournit le label d'une institution
 * 
 * @author asatre
 */
public class InstitutionIdToLabelConverter implements Converter {

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER	= STLogFactory.getLog(InstitutionIdToLabelConverter.class);

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String string) {
		return string;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String id = (String) object;
			try {
				OrganigrammeService organigrammeService = SolonEppServiceLocator.getOrganigrammeService();
				OrganigrammeNode node = organigrammeService.getInstitution(id);

				if (node != null) {
					return node.getLabel();
				} else {
					LOGGER.error(this.getSession(), EppLogEnumImpl.FAIL_GET_INSTITUTION,
							"Impossible de retrouver l'institution " + id);

					return "**institution inconnue**";
				}
			} catch (ClientException e) {
				LOGGER.error(this.getSession(), EppLogEnumImpl.FAIL_GET_INSTITUTION,
						"Impossible de retrouver l'institution " + id, e);
				return null;
			}
		}
		return null;
	}

	private CoreSession getSession() {

		CoreSession session = null;
		try {
			session = SessionUtil.getCoreSession();
		} catch (Exception e) {
		} finally {
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}
		return session;
	}

}
