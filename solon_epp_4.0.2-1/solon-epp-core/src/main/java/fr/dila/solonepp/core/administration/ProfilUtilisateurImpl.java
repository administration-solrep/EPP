package fr.dila.solonepp.core.administration;

import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.api.constant.SolonEppProfilUtilisateurConstants;
import fr.dila.st.api.constant.STProfilUtilisateurConstants;
import fr.dila.st.core.domain.STDomainObjectImpl;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ProfilUtilisateurImpl extends STDomainObjectImpl implements ProfilUtilisateur {
    private static final long serialVersionUID = 713929437623188732L;

    // /////////////////
    // Profil utilisateur protected method
    // ////////////////

    public ProfilUtilisateurImpl(DocumentModel document) {
        super(document);
    }

    @Override
    public Boolean canReceiveNotificationMail() {
        return getBooleanProperty(
            SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            SolonEppProfilUtilisateurConstants.NOTIFICATION_EMAIL_PROPERTY
        );
    }

    @Override
    public void setReceiveNotificationMail(Boolean canReceiveNotificationMail) {
        setProperty(
            SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            SolonEppProfilUtilisateurConstants.NOTIFICATION_EMAIL_PROPERTY,
            canReceiveNotificationMail
        );
    }

    @Override
    public Calendar getDernierChangementMotDePasse() {
        return PropertyUtil.getCalendarProperty(
            document,
            SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY
        );
    }

    @Override
    public void setDernierChangementMotDePasse(Calendar dernierChangementMotDePasse) {
        PropertyUtil.setProperty(
            document,
            SolonEppProfilUtilisateurConstants.PROFIL_UTILISATEUR_SCHEMA,
            STProfilUtilisateurConstants.PROFIL_UTILISATEUR_DERNIER_CHANGEMENT_MDP_PROPERTY,
            dernierChangementMotDePasse
        );
    }

    @Override
    public String getDerniersDossiersIntervention() {
        return null;
    }

    @Override
    public void setDerniersDossiersIntervention(String idDossier) {}
}
