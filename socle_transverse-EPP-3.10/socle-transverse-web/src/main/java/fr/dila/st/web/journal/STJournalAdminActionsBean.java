package fr.dila.st.web.journal;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.Calendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.security.principal.STPrincipal;

/**
 * ActionBean de gestion du journal de l'espace d'administration.
 * 
 * @author BBY
 * @author ARN
 */
@Name("journalAdminActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = FRAMEWORK)
public class STJournalAdminActionsBean extends STJournalActions implements Serializable {

	/**
	 * serial id
	 */
	private static final long	serialVersionUID	= -5649581935468803705L;

	protected String			currentDossierRef;

	public void initialize() {
		super.initialize();

		// Initialisation de la date de début à celle du jour par défaut pour les journaux d'administration
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		currentDateDebut = cal.getTime();
	}

	public void reset() {
		super.reset();
		currentDossierRef = null;
	}

	public String getCurrentDossierRef() {
		return currentDossierRef;
	}

	public void setCurrentDossierRef(String currentDossierRef) {
		this.currentDossierRef = currentDossierRef;
	}

	/**
	 * Vue par défaut à surcharger dans le bean parent
	 * 
	 * @return
	 */
	public String getDefaultContentViewName() {
		return "ADMIN_JOURNAL_DOSSIER";
	};

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		STPrincipal ssPrincipal = (STPrincipal) documentManager.getPrincipal();
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_JOURNAL_READER));
	}

}
