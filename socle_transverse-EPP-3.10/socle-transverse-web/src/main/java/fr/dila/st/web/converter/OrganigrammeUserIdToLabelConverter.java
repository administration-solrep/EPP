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

/**
 * Converter JSF qui fourni le label d'un utilisateur. La valeur d'entrée est soit : - "idUtilisateur", dans ce cas le
 * format de sortie est Civilité Prénom Nom Téléphone - "formattage#idUtilisateur" dans ce cas le formattage est
 * respecté Le formattage est de la forme "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur
 * et Mme. au lieu de Madame) - p : Prénom - n : Nom - t : Téléphone - m : Mail
 * 
 * Par exemple "c p n#54873" pourra donner "Mr Jean Dupont"
 * 
 * @author bgamard
 * 
 */
public class OrganigrammeUserIdToLabelConverter extends AbstractConverter {
	private static final Log	LOGGER	= LogFactory.getLog(OrganigrammeUserIdToLabelConverter.class);

	public OrganigrammeUserIdToLabelConverter() {
		super();
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof String && !StringUtils.isEmpty((String) object)) {
			String value = (String) object;
			String format = null;
			String username = null;
			String[] data = StringUtils.split(value, '#');
			if (data.length != 2) {
				format = "c p n t m";
				if (!value.isEmpty() && ((value.lastIndexOf('#') + 1) != value.length())) {
					username = value;
				}
			} else {
				format = data[0];
				username = data[1];
			}
			try {
				final UserManager userManager = STServiceLocator.getUserManager();
				DocumentModel userDoc = userManager.getUserModel(username);
				if (userDoc == null) {
					return username;
				} else {
					STUser user = userDoc.getAdapter(STUser.class);
					String title = user.getTitle() == null ? "" : user.getTitle();
					String first = user.getFirstName() == null ? "" : user.getFirstName();
					String last = user.getLastName() == null ? "" : user.getLastName();
					String phone = user.getTelephoneNumber() == null ? "" : user.getTelephoneNumber();
					String mail = user.getEmail() == null ? "" : user.getEmail();

					format = format.replace("c", "{c}");
					format = format.replace("C", "{C}");
					format = format.replace("p", "{p}");
					format = format.replace("n", "{n}");
					format = format.replace("t", "{t}");
					format = format.replace("m", "{m}");

					format = format.replace("{c}", title);
					if (format.contains("{C}")) {
						format = format.replace("{C}", title);
						format = format.replace("Monsieur", "M.");
						format = format.replace("Madame", "Mme.");
					}
					format = format.replace("{p}", first);
					format = format.replace("{n}", last);
					format = format.replace("{t}", phone);
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
