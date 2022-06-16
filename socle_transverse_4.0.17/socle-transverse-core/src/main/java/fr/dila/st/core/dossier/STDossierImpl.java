package fr.dila.st.core.dossier;

import fr.dila.cm.cases.HasParticipants;
import fr.dila.cm.cases.HasParticipantsImpl;
import fr.dila.st.api.dossier.STDossier;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation du dossier du socle transverse.
 *
 * @author jtremeaux
 */
public abstract class STDossierImpl implements STDossier {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    protected DocumentModel document;

    private HasParticipants recipientsAdapter;

    /**
     * Constructeur de STDossierImpl.
     *
     * @param dossierDoc
     *            Document dossier
     */

    public STDossierImpl(DocumentModel envelope) {
        document = envelope;
        this.recipientsAdapter = new HasParticipantsImpl(envelope);
    }

    @Override
    public DocumentModel getDocument() {
        return this.document;
    }

    @Override
    public DocumentModel save(CoreSession session) {
        this.document = session.saveDocument(document);
        this.recipientsAdapter = new HasParticipantsImpl(document);
        return document;
    }

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

    @Override
    public boolean isInit() {
        return hasState(DossierState.init);
    }

    @Override
    public boolean isRunning() {
        return hasState(DossierState.running);
    }

    @Override
    public boolean isDone() {
        return hasState(DossierState.done);
    }

    private boolean hasState(DossierState state) {
        return document.getCurrentLifeCycleState().equals(state.name());
    }

    @Override
    public Boolean hasFeuilleRoute() {
        return !StringUtils.isEmpty(getLastDocumentRoute());
    }

    @Override
    public void addInitialExternalParticipants(Map<String, List<String>> recipients) {
        recipientsAdapter.addInitialExternalParticipants(recipients);
    }

    @Override
    public void addInitialInternalParticipants(Map<String, List<String>> recipients) {
        recipientsAdapter.addInitialInternalParticipants(recipients);
    }

    @Override
    public void addParticipants(Map<String, List<String>> recipients) {
        recipientsAdapter.addParticipants(recipients);
    }

    @Override
    public Map<String, List<String>> getAllParticipants() {
        return recipientsAdapter.getAllParticipants();
    }

    @Override
    public Map<String, List<String>> getInitialExternalParticipants() {
        return recipientsAdapter.getInitialExternalParticipants();
    }

    @Override
    public Map<String, List<String>> getInitialInternalParticipants() {
        return recipientsAdapter.getInitialInternalParticipants();
    }
}
