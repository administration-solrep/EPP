package fr.dila.solonepp.web.action.admin;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.service.EtatApplicationService;
import fr.dila.st.core.service.STServiceLocator;

@Name("etatApplicationActions")
@Install(precedence = Install.FRAMEWORK + 1)
public class EtatApplicationActionsBean extends fr.dila.st.web.administration.EtatApplicationActionsBean {

	@Override
	public String editEtatApplication() throws ClientException {
		EtatApplicationService etatApplicationService = STServiceLocator.getEtatApplicationService();
		EtatApplication etatApplication = etatApplicationService.getEtatApplicationDocument(documentManager);
		navigationContext.setCurrentDocument(etatApplication.getDocument());
		navigationContext.setChangeableDocument(etatApplication.getDocument());
		return STViewConstant.ETAT_APPLICATION_VIEW;
	}
}
