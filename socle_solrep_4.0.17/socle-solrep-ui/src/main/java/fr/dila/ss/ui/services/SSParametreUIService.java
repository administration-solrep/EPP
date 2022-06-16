package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.parametres.ParametreList;
import fr.dila.st.ui.th.model.SpecificContext;

public interface SSParametreUIService {
    /**
     * Retourne la liste des paramètres paginés
     * @param context
     * @return
     */
    ParametreList getParametres(SpecificContext context);

    /**
     * Retourne la liste des paramètres de l'archivage paginés
     * @param context
     * @return
     */
    ParametreList getParametresArchive(SpecificContext context);

    /**
     * Sauvegarde le paramètr
     * @param context
     */
    void updateParametre(SpecificContext context);
}
