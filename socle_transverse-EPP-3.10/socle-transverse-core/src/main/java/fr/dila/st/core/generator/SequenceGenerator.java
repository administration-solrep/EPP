package fr.dila.st.core.generator;

import org.nuxeo.ecm.core.api.DocumentException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.uidgen.AbstractUIDGenerator;

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
	public String getSequenceKey(DocumentModel document) throws DocumentException {
		assert document != null;

		return "";
	}

	@Override
	public String createUID(DocumentModel document) throws DocumentException {
		final int index = getNext(document);
		final String seqKey = getSequenceKey(document);

		return seqKey + index;
	}
}
