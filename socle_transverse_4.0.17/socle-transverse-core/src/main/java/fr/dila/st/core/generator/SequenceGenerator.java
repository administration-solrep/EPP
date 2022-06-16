package fr.dila.st.core.generator;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.uidgen.AbstractUIDGenerator;

/**
 * Générateur de séquence simple sans formatage.
 *
 * @author jtremeaux
 */
public class SequenceGenerator extends AbstractUIDGenerator {

    /**
     * Default constructor
     */
    public SequenceGenerator() {
        super();
    }

    @Override
    public String getSequenceKey(DocumentModel document) {
        assert document != null;

        return "";
    }

    @Override
    public String createUID(DocumentModel document) {
        final int index = getNext(document);
        final String seqKey = getSequenceKey(document);

        return seqKey + index;
    }
}
