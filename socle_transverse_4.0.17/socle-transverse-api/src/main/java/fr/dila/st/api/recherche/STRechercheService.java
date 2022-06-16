package fr.dila.st.api.recherche;

import java.util.List;
import org.nuxeo.runtime.model.Component;

public interface STRechercheService extends Component {
    /**
     * Retourne la liste des recherches
     *
     */
    List<Recherche> getRecherches();

    /**
     * Retourne une recherche specifique.
     *
     * @param mode
     *            Un mode de recherche, par exemple simple ou avancee
     * @param type
     *            Le type, à savoir par mots-clés, sur métadonnées, sur les index ...
     * @param targetResourceName
     *            Les ressources sur lesquelles s'effectue la recherche : sur les actes, les dossiers, ...
     *
     */
    Recherche getRecherche(String mode, String type, String targetResourceName);

    /**
     * Retourne une recherche avec un nom donnée en paramêtre.
     *
     * @param rechercheName
     * @return
     */
    Recherche getRecherche(String rechercheName);
}
