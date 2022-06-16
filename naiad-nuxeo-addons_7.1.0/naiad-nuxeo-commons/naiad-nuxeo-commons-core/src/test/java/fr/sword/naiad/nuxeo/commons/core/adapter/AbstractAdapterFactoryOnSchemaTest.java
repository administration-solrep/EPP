package fr.sword.naiad.nuxeo.commons.core.adapter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import fr.sword.naiad.nuxeo.commons.core.constant.NuxeoDocumentTypeConstant;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
public class AbstractAdapterFactoryOnSchemaTest {

	private static String NOTE_TYPE = NuxeoDocumentTypeConstant.TYPE_NOTE;
	private static String FOLDER_TYPE = NuxeoDocumentTypeConstant.TYPE_FOLDER;
	private static String NOTE_SCHEMA = "note";
	
	public static final class Note {
		public DocumentModel document;
		
		public Note(DocumentModel document){
			this.document = document;
		}
	}
	
	public static final class NoteAdapterFactory extends AbstractAdapterFactoryOnSchema {
		public NoteAdapterFactory(){
			super(NOTE_SCHEMA);
		}
		
		@Override
		protected Note adapt(DocumentModel document, Class<?> clazz){
			return new Note(document);
		}
	}
	
	@Inject
	CoreSession session;
	
	@Test
	public void testAdapter() throws Exception {
		
		NoteAdapterFactory naf = new NoteAdapterFactory();
		
		Assert.assertEquals(NOTE_SCHEMA, naf.getSchema());
		
		
		// adapte un document ayant le schema note
		DocumentModel doc = session.createDocumentModel(NOTE_TYPE);
		Assert.assertNotNull(doc);
		
		Note note = (Note)naf.getAdapter(doc, Note.class);
		Assert.assertNotNull(note);
		
		doc = session.createDocumentModel(FOLDER_TYPE);
		Assert.assertNotNull(doc);
		
		// tentative sur un document n'ayant pas le schema
		note = (Note)naf.getAdapter(doc, Note.class);
		Assert.assertNull(note);
		
	} 
	
}
