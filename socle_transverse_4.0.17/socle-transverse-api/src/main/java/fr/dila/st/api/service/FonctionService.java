package fr.dila.st.api.service;

import fr.dila.st.api.user.BaseFunction;

/**
 * Interface du service permettant de gérer les fonctions.
 *
 */
public interface FonctionService {
    BaseFunction getFonction(String fonctionName);
}
