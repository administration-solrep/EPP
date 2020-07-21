package fr.dila.st.web.plateform.audit.ejb;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.nuxeo.ecm.platform.audit.api.Logs;
import org.nuxeo.ecm.platform.audit.api.remote.LogsRemote;
import org.nuxeo.ecm.platform.audit.ejb.LogsBean;
import org.nuxeo.ecm.platform.audit.service.NXAuditEventsService;

import fr.dila.st.core.service.STServiceLocator;

/**
 * surcharge de la classe LogsBean de nuxeo : permet de stocker en dur le nom des utilisateurs
 * 
 * @author arolin
 */
@Stateless
@Remote(LogsRemote.class)
public class STLogsBean extends LogsBean implements Logs {

	@Override
	protected NXAuditEventsService service() {
		return STServiceLocator.getSTAuditEventsService();
	}
}
