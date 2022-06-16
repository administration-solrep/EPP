package fr.dila.st.ui.services.impl;

import static fr.dila.st.core.service.STServiceLocator.getEtatApplicationService;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.GestionAccesDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.mapper.MapDoc2Bean;
import fr.dila.st.ui.services.EtatApplicationUIService;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class EtatApplicationUIServiceImpl implements EtatApplicationUIService {

    @Override
    public void restrictAccess(SpecificContext context) {
        CoreSession session = context.getSession();
        getEtatApplicationService().restrictAccess(session, "Accès restreint aux administrateurs");
    }

    @Override
    public void restoreAccess(SpecificContext context) {
        CoreSession session = context.getSession();
        getEtatApplicationService().restoreAccess(session);
    }

    @Override
    public boolean isAccessRestricted(SpecificContext context) {
        CoreSession session = context.getSession();
        EtatApplication etatApplication = getEtatApplicationService().getEtatApplicationDocument(session);
        return etatApplication.getRestrictionAcces();
    }

    @Override
    public GestionAccesDTO getEtatApplicationDocument(SpecificContext context) {
        CoreSession session = context.getSession();
        EtatApplication etatApplication = STServiceLocator
            .getEtatApplicationService()
            .getEtatApplicationDocument(session);
        return MapDoc2Bean.docToBean(etatApplication.getDocument(), GestionAccesDTO.class);
    }

    @Override
    public Map<String, Object> getEtatApplicationDocumentUnrestricted() {
        return STServiceLocator.getEtatApplicationService().getRestrictionAccesUnrestricted();
    }

    @Override
    public void updateDocument(SpecificContext context) {
        CoreSession session = context.getSession();
        GestionAccesDTO dto = context.getFromContextData(STContextDataKey.GESTION_ACCES);

        EtatApplication etatApplication = STServiceLocator
            .getEtatApplicationService()
            .getEtatApplicationDocument(session);
        DocumentModel doc = etatApplication.getDocument();
        MapDoc2Bean.beanToDoc(dto, doc);
        session.saveDocument(doc);

        context
            .getMessageQueue()
            .addMessageToQueue(ResourceHelper.getString("st.restriction.access.message"), AlertType.TOAST_INFO);
    }

    @Override
    public boolean isAccessAuthorized(SpecificContext context) {
        CoreSession session = context.getSession();
        STPrincipal ssPrincipal = (STPrincipal) session.getPrincipal();
        return ssPrincipal.isAdministrator() || ssPrincipal.isMemberOf("AccessUnrestrictedUpdater");
    }
}
