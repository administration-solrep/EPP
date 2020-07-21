package fr.dila.solonepp.web.action;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.directory.VocabularyEntry;

import fr.dila.solonepp.api.constant.SolonEppVocabularyConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.logging.enumerationCodes.EppLogEnumImpl;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.SolonEppVocabularyService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.web.action.corbeille.CorbeilleActionsBean;
import fr.dila.solonepp.web.action.evenement.EvenementActionsBean;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.StringUtil;

@Name("rubriquesActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class RubriquesActionsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1030407654537892673L;
	private static final STLogger LOGGER = STLogFactory.getLog(RubriquesActionsBean.class);
	
	@In(required = true, create = true)
	protected EppPrincipal eppPrincipal;

	@In(create = true, required = false)
	protected transient CorbeilleActionsBean corbeilleActions;
	
	@In(create = true, required = false)
	protected transient CoreSession documentManager;	

	@In(create = true, required = false)
	protected transient EvenementActionsBean evenementActions;
	
	protected List<VocabularyEntry> rubriquesValues;

	private void loadRubriquesValuesForUser() {
		DocumentModel evenementDoc = evenementActions.getCurrentEvenementForCreation();
		final SolonEppVocabularyService eppService = SolonEppServiceLocator.getSolonEppVocabularyService();
		final List<InstitutionsEnum> institutionsList = new ArrayList<InstitutionsEnum>();
		if (evenementDoc == null) {
			final Set<String> institutions = eppPrincipal.getInstitutionIdSet();
			for(String inst : institutions) {
				institutionsList.add(InstitutionsEnum.valueOf(inst));
			}
		} else {
			Evenement evenement = evenementDoc.getAdapter(Evenement.class);
			String emetteur = evenement.getEmetteur();
			if (StringUtil.isNotBlank(emetteur)) {
				institutionsList.add(InstitutionsEnum.valueOf(emetteur));
			}
		}
		rubriquesValues = eppService.getRubriquesEntriesForEmetteurs(institutionsList);
	}
	
	public String getLabel() {
		SolonEppVocabularyService eppService = SolonEppServiceLocator.getSolonEppVocabularyService();
		DocumentModel versionDoc = null;
		try {
			versionDoc = corbeilleActions.getSelectedVersion();
		} catch (ClientException e) {
			LOGGER.error(documentManager, EppLogEnumImpl.FAIL_GET_VERSION_FONC, e);
		}
		
		if (versionDoc != null) {
			Version version = versionDoc.getAdapter(Version.class);
			if (version.getRubrique() != null) {
				return eppService.getEntryLabel(SolonEppVocabularyConstant.RUBRIQUES_VOCABULARY, version.getRubrique());
			}
		}
		return "";
	}
	
	@Observer("emetteurChangeEvent")
	public void reloadRubriques() {
		LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "relaod rubriques");
		loadRubriquesValuesForUser();
	}
	
	/**
	 * Get the current fond de dossier Document.
	 */
	@Factory(value = "rubriquesValues", scope = EVENT)
	public List<VocabularyEntry> getRubriquesValues() {
		loadRubriquesValuesForUser();
		return rubriquesValues;
	}
}
