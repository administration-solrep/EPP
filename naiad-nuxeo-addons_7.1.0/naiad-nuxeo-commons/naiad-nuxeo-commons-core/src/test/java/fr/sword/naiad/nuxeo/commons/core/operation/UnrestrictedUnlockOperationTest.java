package fr.sword.naiad.nuxeo.commons.core.operation;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

import fr.sword.naiad.nuxeo.commons.core.constant.NuxeoDocumentTypeConstant;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({	
	"org.nuxeo.ecm.automation.core",
	"fr.sword.naiad.nuxeo.commons.core"})
public class UnrestrictedUnlockOperationTest {

	@Inject
	CoreSession session;
	
	@Test
	public void testUnlock() throws Exception {
		
		DocumentModel doc = session.createDocumentModel(NuxeoDocumentTypeConstant.TYPE_FILE);
		doc = session.createDocument(doc);
		DocumentRef docRef = doc.getRef();		
		Assert.assertNull(session.getLockInfo(docRef));
		
		callUnlockOperation(docRef);		
		Assert.assertNull(session.getLockInfo(docRef));
		
		session.setLock(docRef);		
		Assert.assertNotNull(session.getLockInfo(docRef));
		
		callUnlockOperation(docRef);
		Assert.assertNull(session.getLockInfo(docRef));
	}
	
	private DocumentModel callUnlockOperation(DocumentRef docRef) throws Exception {
		DocumentModel input = session.getDocument(docRef);
		Map<String, Object> params = new HashMap<String, Object>();
		AutomationService service = ServiceUtil.getRequiredService(AutomationService.class);
		OperationContext ctx = new OperationContext(session);
		ctx.setInput(input);
		return (DocumentModel) service.run(ctx, UnrestrictedUnlockOperation.OPID, params);
	}

}
