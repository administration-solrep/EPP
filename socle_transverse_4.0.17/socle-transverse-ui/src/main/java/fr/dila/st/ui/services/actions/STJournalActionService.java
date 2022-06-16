package fr.dila.st.ui.services.actions;

import java.util.Set;

public interface STJournalActionService {
    /**
     * Retourne le contenu de la chaine d'entrée traduite mot à mot
     *
     */
    String translate(String entry);

    /**
     * Affiche les postes d'un utilisateur à partir de son identifiant.
     *
     * @param string
     * @return String listes des postes
     */
    String getPostesLabels(String idUser);

    // Getters et Setters

    /**
     * Retourne la liste des categories
     *
     * @return Set<String>
     */
    Set<String> getCategoryList();
}
