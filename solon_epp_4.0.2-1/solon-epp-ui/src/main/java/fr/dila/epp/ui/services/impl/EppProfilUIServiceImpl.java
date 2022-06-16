package fr.dila.epp.ui.services.impl;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.EppProfilUIService;
import fr.dila.epp.ui.th.bean.ProfilForm;
import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import org.apache.commons.lang3.BooleanUtils;

public class EppProfilUIServiceImpl implements EppProfilUIService {

    @Override
    public void saveProfil(SpecificContext context) {
        ProfilForm form = context.getFromContextData(EppContextDataKey.PROFIL_FORM);

        ProfilUtilisateur profil = SolonEppServiceLocator
            .getProfilUtilisateurService()
            .getOrCreateCurrentUserProfil(context.getSession())
            .getAdapter(ProfilUtilisateur.class);
        profil.setReceiveNotificationMail(BooleanUtils.isTrue(form.getMailNotification()));
        context.getSession().saveDocument(profil.getDocument());

        context.getMessageQueue().addInfoToQueue(ResourceHelper.getString("profil.success.update"));
    }

    @Override
    public ProfilForm getProfil(SpecificContext context) {
        ProfilUtilisateur profil = SolonEppServiceLocator
            .getProfilUtilisateurService()
            .getOrCreateCurrentUserProfil(context.getSession())
            .getAdapter(ProfilUtilisateur.class);

        ProfilForm form = new ProfilForm();
        form.setMailNotification(profil.canReceiveNotificationMail());

        return form;
    }
}
