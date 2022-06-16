package fr.dila.reponses.rest.client;

import java.util.List;

import javax.xml.bind.JAXBException;

import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.NotificationType;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.TraitementStatut;

/**
 * 
 * @author sly
 * 
 */
public class WSNotificationCaller {

	private final WSNotification	notificationService;

	public WSNotificationCaller(WSNotification notificationService) {
		super();
		this.notificationService = notificationService;
	}

	public static EnvoyerNotificationRequest buildRequest(NotificationType typeNotification, String jeton,
			List<QuestionId> qids) {

		EnvoyerNotificationRequest enr = new EnvoyerNotificationRequest();

		if (jeton != null && jeton.length() > 0 && qids == null) {
			enr.setJeton(jeton);
		} else if (qids != null && qids.size() > 0 && jeton == null) {
			enr.getIdQuestions().addAll(qids);
		} else {
			return null;
		}
		enr.setTypeNotification(typeNotification);

		return enr;

	}

	public EnvoyerNotificationResponse notifier(EnvoyerNotificationRequest request) throws Exception {

		// --- handle transaction
		EnvoyerNotificationResponse response;

		response = notificationService.envoyerNotification(request);

		// --- check response
		if (response == null || response.getStatutTraitement() == null) {
			throw new WSNotificationCallerException("null response to notification");
		}

		TraitementStatut ts = response.getStatutTraitement();

		if (ts != TraitementStatut.OK) {
			StringBuffer sb = new StringBuffer();
			sb.append("bad responnse status " + TraitementStatut.OK.toString());

			try {
				sb.append("response was \n");
				sb.append(JaxBHelper.marshallToString(response, EnvoyerNotificationResponse.class));
			} catch (JAXBException e) {
				sb.append("  could not unmarshall response");
			}
			throw new WSNotificationCallerException(sb.toString());
		}

		return response;
	}

}
