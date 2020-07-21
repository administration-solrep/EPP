package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.st.api.user.STUser;
import fr.dila.st.core.service.STServiceLocator;

/**
 * TODO à déplacer dans STUserService ou OrganigrammsService -- jtx
 */
public final class UserUtil {

	private static final Log	LOG	= LogFactory.getLog(UserUtil.class);

	/**
	 * utility class
	 */
	private UserUtil() {
		// do noting
	}

	/**
	 * Retourne une liste d'utilisateurs à partir d'une liste d'identifiants.
	 * 
	 * @param recipients
	 * @return
	 * @throws ClientException
	 */
	public static List<STUser> getUserListFromIds(List<String> userIds) throws ClientException {
		final List<STUser> users = new ArrayList<STUser>();
		final UserManager userManager = STServiceLocator.getUserManager();
		for (String userId : userIds) {
			DocumentModel userDoc = null;
			try {
				userDoc = userManager.getUserModel(userId);
			} catch (ClientException e) {
				LOG.error("Failed to retrieve user [" + userId + "]", e);
			}
			if (userDoc != null) {
				final STUser user = userDoc.getAdapter(STUser.class);
				users.add(user);
			}
		}
		return users;
	}

}
