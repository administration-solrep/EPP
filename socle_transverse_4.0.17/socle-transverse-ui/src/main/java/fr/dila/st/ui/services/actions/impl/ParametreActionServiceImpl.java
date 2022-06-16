package fr.dila.st.ui.services.actions.impl;

import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEUR;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_LIBELLE;
import static fr.dila.st.core.service.STServiceLocator.getConfigService;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.services.actions.ParametreActionService;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ParametreActionServiceImpl implements ParametreActionService {

    @Override
    public void updateDocument(SpecificContext context, DocumentModel doc) {
        context.getSession().saveDocument(doc);
        context.getSession().save();
        context.getMessageQueue().addInfoToQueue("st.parametre.modified");
    }

    @Override
    public String getParameterValue(CoreSession session, String parameter) {
        return STServiceLocator.getSTParametreService().getParametreValue(session, parameter);
    }

    @Override
    public String getIdentificationPlateformeLibelle(SpecificContext context) {
        if (!context.getContextData().containsKey(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE)) {
            context
                .getContextData()
                .put(
                    SOLON_IDENTIFICATION_PLATEFORME_LIBELLE,
                    getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE)
                );
        }
        return context.getContextData().get(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE).toString();
    }

    @Override
    public String getIdentificationPlateformeCouleur(SpecificContext context) {
        if (!context.getContextData().containsKey(SOLON_IDENTIFICATION_PLATEFORME_COULEUR)) {
            context
                .getContextData()
                .put(
                    SOLON_IDENTIFICATION_PLATEFORME_COULEUR,
                    getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEUR)
                );
        }
        return context.getContextData().get(SOLON_IDENTIFICATION_PLATEFORME_COULEUR).toString();
    }
}
