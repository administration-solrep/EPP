package fr.dila.st.ui.services;

import fr.dila.st.core.requeteur.RequeteurRechercheUtilisateurs;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.th.model.SpecificContext;

public interface STRechercheUtilisateursUIService {
    STUsersList searchUsers(SpecificContext context);

    void deleteUsers(SpecificContext context);

    /**
     * Envoi d'un mail à la liste des utilisateurs passés en paramètre. Les utilisateurs sont en BCC.
     *
     * @param context SpecificContext
     */
    void envoyerMail(SpecificContext context);

    void createExportExcel(SpecificContext context);

    RequeteurRechercheUtilisateurs getUserRequeteur(SpecificContext context);
}
