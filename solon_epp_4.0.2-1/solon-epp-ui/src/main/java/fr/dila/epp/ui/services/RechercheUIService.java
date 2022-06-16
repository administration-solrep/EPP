package fr.dila.epp.ui.services;

import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.bean.RechercheDynamique;
import fr.dila.st.ui.th.model.SpecificContext;

public interface RechercheUIService {
    /**
     * Retourne la liste des widgets pour la catégorie d'événements
     *
     * @param ID catégorie d'événements
     * @return
     */
    RechercheDynamique getRechercheDynamique(SpecificContext context);

    /**
     * Retourne la liste des communications correspondant à la recherche
     *
     * @param JSON_SEARCH critères de la recherche
     * @param MESSAGE_LIST_FORM formulaire de la liste de communications
     * @return
     */
    MessageList getResultatsRecherche(SpecificContext context);
}
