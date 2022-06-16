package fr.dila.epp.ui.services.actions;

import fr.dila.solonepp.api.descriptor.evenementtype.PieceJointeDescriptor;
import fr.dila.st.ui.bean.VersionSelectDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface CorbeilleActionService {
    /**
     * Retourne la requête pour récupérer la liste des messages
     *
     * @param context
     * @return
     */
    String getMessageListQueryString(SpecificContext context);

    /**
     * Retourne les paramêtres de la requête pour récupérer la liste des messages
     *
     * @param context
     * @return
     */
    List<Object> getMessageListQueryParameter(SpecificContext context);

    /**
     * Retourne la version selectionnée par l'utilisateur de l'événement en cours d'affichage, la dernière par défaut
     *
     * @param context
     * @return
     */
    DocumentModel getSelectedVersion(SpecificContext context);

    /**
     * Retourne les types de pièces jointes qui correspondent à l'événement courant
     *
     * @param context
     * @return
     */
    Set<String> getListTypePieceJointe(SpecificContext context);

    /**
     * Retourne les types de pièces jointes et leurs descriptors qui correspondent à l'événement courant
     *
     * @param context
     * @return
     */
    Map<String, PieceJointeDescriptor> getMapTypePieceJointe(SpecificContext context);

    List<VersionSelectDTO> getallVersions(SpecificContext context);
}
