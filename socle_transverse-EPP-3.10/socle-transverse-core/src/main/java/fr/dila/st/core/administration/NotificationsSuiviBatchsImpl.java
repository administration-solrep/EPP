package fr.dila.st.core.administration;

import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.STDomainObjectImpl;

/**
 * Adapter Document Notifications Suivi des Batchs
 * 
 * @author JBT
 * 
 */
public class NotificationsSuiviBatchsImpl extends STDomainObjectImpl implements NotificationsSuiviBatchs {

	/**
	 * Serial version UID.
	 */
	private static final long	serialVersionUID	= -6420773701899286710L;

	public NotificationsSuiviBatchsImpl(DocumentModel doc) {
		super(doc);
	}

	@Override
	public boolean getEtatNotification() {
		return getBooleanProperty(STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA,
				STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_ETAT_NOTIFICATION_PROPERTY);
	}

	@Override
	public void setEtatNotification(boolean etatNotification) {
		setProperty(STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA,
				STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_ETAT_NOTIFICATION_PROPERTY, etatNotification);
	}

	@Override
	public List<String> getReceiverMailList() {
		return getListStringProperty(STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA,
				STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_LIST_USERNAME_PROPERTY);
	}

	@Override
	public void setReceiverMailList(List<String> receiverMailList) {
		setProperty(STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA,
				STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_LIST_USERNAME_PROPERTY, receiverMailList);
	}

}
