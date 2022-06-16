package fr.dila.st.ui.services.mail;

import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface MailUIService {
    /**
     * Récupère les adresses d'émission disponibles et crée une liste de
     * SelectValueDTO à partir d'elles.
     */
    List<SelectValueDTO> retrieveAdresseEmissionValues(SpecificContext context);
}
