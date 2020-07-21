package fr.dila.solonepp.api.administration;

import java.io.Serializable;

import fr.dila.st.api.domain.STDomainObject;
import fr.dila.st.api.user.STProfilUtilisateur;

public interface ProfilUtilisateur extends STProfilUtilisateur,STDomainObject, Serializable {

    Boolean isDesactivedNotificationMail();

    void setDesactivedNotificationMail(Boolean desactiveNotificationMail);
}
