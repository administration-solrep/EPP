package fr.dila.epp.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface VersionUIService {
    /**
     * Retourne la liste des actions possible sur la version courante
     *
     * @param CURRENT_MESSAGE_DOC message courant
     * @param CURRENT_VERSION_DOC version courante
     * @return Liste d'ids d'actions
     */
    List<String> getActionList(SpecificContext context);

    /**
     * Retourne true si l'action est autorisée
     *
     * @param ID id de l'action
     * @param CURRENT_MESSAGE_DOC message courant
     * @param CURRENT_VERSION_DOC version courante
     * @return
     */
    boolean isActionPossible(SpecificContext context);

    /**
     * Retourne le message de confirmation à afficher à l'utilisateur
     *
     * @param CURRENT_VERSION_DOC version courante
     * @return
     */
    String getConfirmMessageAccepter(SpecificContext context);

    /**
     * Retourne le message de confirmation à afficher à l'utilisateur
     *
     * @param CURRENT_VERSION_DOC version courante
     * @return
     */
    String getConfirmMessageRejeter(SpecificContext context);

    /**
     * Retourne le message de confirmation à afficher à l'utilisateur
     *
     * @param CURRENT_VERSION_DOC version courante
     * @return
     */
    String getConfirmMessageAbandonner(SpecificContext context);
}
