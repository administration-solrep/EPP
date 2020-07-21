package fr.dila.st.web.corbeille;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * WebBean permettant de gérer les corbeilles de l'application.
 * 
 * @author admin
 */
@Name("corbeilleActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public abstract class CorbeilleActionsBean implements Serializable {
	private static final long	serialVersionUID	= -6601690797613742328L;

	/**
	 * Retourne vrai si le dossier est présent dans une Mailbox et chargé en session, c'est-à-dire que l'utilisateur
	 * peut agir sur le dossier.
	 * 
	 * @return Vrai si l'utilisateur peut agir sur le dossier
	 */
	public abstract boolean isDossierLoadedInCorbeille();
}
