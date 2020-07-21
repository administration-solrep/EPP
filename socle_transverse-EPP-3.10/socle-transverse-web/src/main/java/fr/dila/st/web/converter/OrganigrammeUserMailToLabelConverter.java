package fr.dila.st.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

public class OrganigrammeUserMailToLabelConverter extends AbstractConverter {
	private static final Log	LOGGER	= LogFactory.getLog(OrganigrammeUserMailToLabelConverter.class);

	public OrganigrammeUserMailToLabelConverter() {
		super();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String value = (String) object;
			String format = null;
			String username = null;
			String[] data = StringUtils.split(value, '#');
			if (data.length == 2) {
				format = data[0];
				username = data[1];
			} else {
				format = "m";
				if (!value.isEmpty() && ((value.lastIndexOf('#') + 1) != value.length())) {
					username = value;
				}
			}
			try {
				final UserManager userManager = STServiceLocator.getUserManager();
				DocumentModel userDoc = userManager.getUserModel(username);
				if (userDoc == null) {
					return username;
				} else {
					STUser user = userDoc.getAdapter(STUser.class);
					String mail = user.getEmail() == null ? "" : user.getEmail();
					format = format.replace("m", "{m}");
					format = format.replace("{m}", mail);
					return format.trim();
				}
			} catch (ClientException ce) {
				LOGGER.error("Erreur de récupération du DocumentModel de l'utilisateur " + username, ce);
			}
		}
		return null;
	}
}
