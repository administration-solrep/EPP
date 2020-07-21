package fr.dila.solonepp.web.document;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.solonepp.api.domain.dossier.Dossier;

/**
 * Ce WebBean permet d'injecter les classes des objets métiers pour les rendre disponible dans le contexte
 * Seam.
 * 
 * @author jtremeaux
 */
@Name("documentModelActions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = FRAMEWORK + 1)
public class DocumentModelActionsBean extends fr.dila.st.web.document.DocumentModelActionsBean implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Retourne l'interface de l'objet métier Dossier.
     * 
     * @return Interface de l'objet métier
     */
    @Factory(value = "Dossier", scope = ScopeType.APPLICATION)
    public Class<Dossier> getDossier() {
        return Dossier.class;
    }
    
}
