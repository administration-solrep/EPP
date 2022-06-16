package fr.dila.epp.ui.services;

import fr.dila.epp.ui.bean.DetailDossier;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface MetadonneesUIService {
    /**
     * Retourne la liste des {@link WidgetDTO} correspondant à l'événement
     * courant
     *
     * CURRENT_VERSION_DOC version courante
     *
     * @param context
     * @return
     */
    List<WidgetDTO> getWidgetListForCommunication(SpecificContext context);

    /**
     * Retourne le {@link DetailDossier} correspondant au dossier courant
     *
     * CURRENT_VERSION_DOC version courante
     *
     * @param context
     * @return
     */
    DetailDossier getDetailDossier(SpecificContext context);

    /**
     * Retourne l'institution qui convient comme destinataire copie
     *
     * TYPE_EVENEMENT
     * EMETTEUR
     * DESTINATAIRE
     *
     * @param context
     * @return
     */
    SelectValueDTO getDestinataireCopie(SpecificContext context);
}
