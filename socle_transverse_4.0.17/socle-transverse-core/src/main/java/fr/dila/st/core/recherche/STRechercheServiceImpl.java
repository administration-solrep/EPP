package fr.dila.st.core.recherche;

import fr.dila.st.api.recherche.Recherche;
import fr.dila.st.api.recherche.STRechercheService;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * L'implémentation du service de recherche du socle transverse. Utilisé par la recherche de Réponses et la recherche de
 * dossier simple d'EPG
 *
 * @author jgomez
 *
 */
public class STRechercheServiceImpl extends DefaultComponent implements STRechercheService {
    private static final Object RECHERCHE_EP = "recherches";

    private List<Recherche> recherches;

    public STRechercheServiceImpl() {
        super();
        recherches = new ArrayList<Recherche>();
    }

    @Override
    public List<Recherche> getRecherches() {
        if (recherches == null) {
            return new ArrayList<Recherche>();
        }
        return recherches;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (RECHERCHE_EP.equals(extensionPoint)) {
            Recherche recherche = (Recherche) contribution;
            recherches.add(recherche);
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (RECHERCHE_EP.equals(extensionPoint)) {
            recherches.remove(contribution);
        }
    }

    @Override
    public Recherche getRecherche(String mode, String type, String targetResourceName) {
        for (Recherche recherche : recherches) {
            if (
                recherche.getMode().equals(mode) &&
                recherche.getTargetResourceName().equals(targetResourceName) &&
                recherche.getType().equals(type)
            ) {
                return recherche;
            }
        }
        return null;
    }

    @Override
    public Recherche getRecherche(String rechercheName) {
        for (Recherche recherche : recherches) {
            if (recherche.getRechercheName().equals(rechercheName)) {
                return recherche;
            }
        }
        return null;
    }
}
