package fr.dila.st.core.override.listener;

import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.BEFORE_DOC_UPDATE;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_CREATED;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_PUBLISHED;
import static org.nuxeo.ecm.core.api.event.DocumentEventTypes.DOCUMENT_UPDATED;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.st.core.schema.DublincoreSchemaUtils;

/**
 * Listener pour le dublincore en surcharge de celui de nuxeo
 * 
 */
public class DublinCoreListener implements EventListener {

	private static final Log			LOGGER			= LogFactory.getLog(DublinCoreListener.class);

	private static final List<String>	EVENT_LISTENED	= Arrays.asList(new String[] { DOCUMENT_UPDATED,
			DOCUMENT_CREATED, BEFORE_DOC_UPDATE, DOCUMENT_PUBLISHED });

	/**
	 * Default constructor
	 */
	public DublinCoreListener() {
		super();
	}

	/**
	 * Core event notification.
	 * <p>
	 * Gets core events and updates DublinCore if needed.
	 * 
	 * @param event
	 *            event fired at core layer
	 */
	@Override
	public void handleEvent(final Event event) throws ClientException {

		if (!(event.getContext() instanceof DocumentEventContext)) {
			return;
		}

		DocumentEventContext docCtx = (DocumentEventContext) event.getContext();
		final String eventId = event.getName();

		if (!EVENT_LISTENED.contains(eventId)) {
			return;
		}

		final DocumentModel doc = docCtx.getSourceDocument();

		if (doc.isVersion()) {
			LOGGER.debug("No DublinCore update on versions except for the issued date");
			return;
		}

		final Date eventDate = new Date(event.getTime());
		final Calendar cEventDate = Calendar.getInstance();
		cEventDate.setTime(eventDate);

		if (eventId.equals(BEFORE_DOC_UPDATE)) {
			setModificationDate(doc, cEventDate, docCtx.getPrincipal().getName());
		} else if (eventId.equals(DOCUMENT_CREATED)) {
			DublincoreSchemaUtils.setCreatedDate(doc, cEventDate);
			setModificationDate(doc, cEventDate, docCtx.getPrincipal().getName());
			DublincoreSchemaUtils.setCreator(doc, docCtx.getPrincipal().getName());
		}
	}

	/**
	 * Met à jour la date de modification et renseigne la date de création si elle est null
	 * 
	 * @param doc
	 * @param modificationDate
	 * @throws ClientException
	 */
	protected void setModificationDate(final DocumentModel doc, final Calendar modificationDate,
			final String lastContributor) throws ClientException {
		DublincoreSchemaUtils.setModifiedDate(doc, modificationDate);
		if (DublincoreSchemaUtils.getCreatedDate(doc) == null) {
			DublincoreSchemaUtils.setCreatedDate(doc, modificationDate);
		}
		DublincoreSchemaUtils.setLastContributor(doc, lastContributor);
	}
}
