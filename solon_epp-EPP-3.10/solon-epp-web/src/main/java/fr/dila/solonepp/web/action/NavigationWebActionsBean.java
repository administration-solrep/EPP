package fr.dila.solonepp.web.action;

import java.io.Serializable;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("navigationWebActions")
@Scope(ScopeType.CONVERSATION)
@SerializedConcurrentAccess
@Install(precedence = Install.APPLICATION + 1)
public class NavigationWebActionsBean extends fr.dila.st.web.action.NavigationWebActionsBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Comportements du panneau supérieur de recherche rapide
     */
    protected Boolean upperRechercheRapidePanelIsOpened = false;
    
    
    public NavigationWebActionsBean() {
        super();
    }

	public Boolean getUpperRechercheRapidePanelIsOpened() {
		return upperRechercheRapidePanelIsOpened;
	}

	public void setUpperRechercheRapidePanelIsOpened(Boolean upperRechercheRapidePanelIsOpened) {
		this.upperRechercheRapidePanelIsOpened = upperRechercheRapidePanelIsOpened;
	}
    
}
