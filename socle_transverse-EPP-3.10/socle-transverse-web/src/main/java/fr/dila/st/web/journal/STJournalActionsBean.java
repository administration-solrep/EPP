package fr.dila.st.web.journal;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * ActionBean de gestion du journal.
 * 
 * @author BBY
 * @author ARN
 */
@Name("journalActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK)
public class STJournalActionsBean extends STJournalActions implements Serializable {

	/**
	 * serial id
	 */
	private static final long	serialVersionUID	= -5649581935468803705L;

	/**
	 * Vue par défaut à surcharger dans le bean parent
	 * 
	 * @return
	 */
	public String getDefaultContentViewName() {
		return "JOURNAL_DOSSIER";
	};

}
