package fr.dila.st.api.user;

import fr.dila.st.api.domain.STDomainObject;
import java.util.Calendar;

public interface STProfilUtilisateur extends STDomainObject {
    /**
     * @return la date à laquelle le mot de passe a été changé
     */
    Calendar getDernierChangementMotDePasse();

    /**
     * @param dernierChangementMotDePasse
     *            La date à laquelle le mot de passe a été changé
     */
    void setDernierChangementMotDePasse(Calendar dernierChangementMotDePasse);

    String getDerniersDossiersIntervention();

    void setDerniersDossiersIntervention(String idDossier);
}
