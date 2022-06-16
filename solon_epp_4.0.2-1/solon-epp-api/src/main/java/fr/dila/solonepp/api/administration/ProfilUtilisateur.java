package fr.dila.solonepp.api.administration;

import fr.dila.st.api.domain.STDomainObject;
import fr.dila.st.api.user.STProfilUtilisateur;
import java.io.Serializable;

public interface ProfilUtilisateur extends STProfilUtilisateur, STDomainObject, Serializable {
    Boolean canReceiveNotificationMail();

    void setReceiveNotificationMail(Boolean canReceiveNotificationMail);
}
