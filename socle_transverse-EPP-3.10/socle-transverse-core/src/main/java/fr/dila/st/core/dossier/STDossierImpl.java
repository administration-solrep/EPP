package fr.dila.st.core.dossier;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.cases.CaseImpl;
import fr.dila.cm.cases.HasParticipants;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Implémentation du dossier du socle transverse.
 * 
 * @author jtremeaux
 */
abstract public class STDossierImpl extends CaseImpl implements STDossier {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	protected String getStringProperty(String schema, String value) {
		return PropertyUtil.getStringProperty(document, schema, value);
	}

	protected List<String> getListStringProperty(String schema, String value) {
		return PropertyUtil.getStringListProperty(document, schema, value);
	}

	protected Long getLongProperty(String schema, String value) {
		return PropertyUtil.getLongProperty(document, schema, value);
	}

	protected void setProperty(String schema, String property, Object value) {
		PropertyUtil.setProperty(document, schema, property, value);
	}

	protected Calendar getDateProperty(String schema, String value) {
		return PropertyUtil.getCalendarProperty(document, schema, value);
	}

	/**
	 * Constructeur de STDossierImpl.
	 * 
	 * @param dossierDoc
	 *            Document dossier
	 */
	public STDossierImpl(DocumentModel dossierDoc) {
		super(dossierDoc, dossierDoc.getAdapter(HasParticipants.class));
	}

	@Override
	public boolean isInit() throws ClientException {
		return document.getCurrentLifeCycleState().equals(DossierState.init.name());
	}

	@Override
	public boolean isRunning() throws ClientException {
		return document.getCurrentLifeCycleState().equals(DossierState.running.name());
	}

	@Override
	public boolean isDone() throws ClientException {
		return document.getCurrentLifeCycleState().equals(DossierState.done.name());
	}

	@Override
	public Boolean hasFeuilleRoute() {
		return !StringUtils.isEmpty(getLastDocumentRoute());
	}
}
