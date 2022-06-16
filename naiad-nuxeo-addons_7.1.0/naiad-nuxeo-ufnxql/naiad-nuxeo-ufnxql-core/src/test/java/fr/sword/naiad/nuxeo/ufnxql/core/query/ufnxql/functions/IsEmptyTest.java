package fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;

import com.google.inject.Inject;

import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import fr.sword.naiad.nuxeo.commons.test.runner.OrderedRunner;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMakerFeature;
import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMakerRepositoryInit;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;

@RunWith(OrderedRunner.class)
@RepositoryConfig(init = FlexibleQueryMakerRepositoryInit.class, cleanup = Granularity.METHOD)
@Features(FlexibleQueryMakerFeature.class)
public class IsEmptyTest {

	@Inject
	protected CoreSession session;
	
	public static final String DOC_TYPE = "MaNote";
	
	@Test
	public void testIsEmpty() throws NuxeoException {
		DocumentModel emptyDoc = session.createDocumentModel("/", "empty", DOC_TYPE);
		emptyDoc = session.createDocument(emptyDoc);
		
		DocumentModel notemptyDoc = session.createDocumentModel("/", "notempty", DOC_TYPE);
		DublincorePropertyUtil.setSubjects(notemptyDoc, new String[]{"subject1"});
		notemptyDoc = session.createDocument(notemptyDoc);
		
		session.save();
		
		DocumentModelList dml = QueryUtils.doQuery(session, QueryUtils.ufnxqlToFnxqlQuery("SELECT d.ecm:uuid AS id FROM " + DOC_TYPE + " AS d"), -1, 0);
		Assert.assertEquals(2, dml.size());
		
		dml = QueryUtils.doQuery(session, QueryUtils.ufnxqlToFnxqlQuery("SELECT d.ecm:uuid AS id FROM " + DOC_TYPE + " AS d WHERE isEmpty(d.dc:subjects) = 1"), -1, 0);
		Assert.assertEquals(1, dml.size());
		Assert.assertEquals(emptyDoc.getId(), dml.get(0).getId());
		
		dml = QueryUtils.doQuery(session, QueryUtils.ufnxqlToFnxqlQuery("SELECT d.ecm:uuid AS id FROM " + DOC_TYPE + " AS d WHERE isEmpty(d.dc:subjects) = 0"), -1, 0);
		Assert.assertEquals(1, dml.size());
		Assert.assertEquals(notemptyDoc.getId(), dml.get(0).getId());
	}
	
}
