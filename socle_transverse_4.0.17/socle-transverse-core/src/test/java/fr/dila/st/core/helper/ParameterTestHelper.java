package fr.dila.st.core.helper;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.core.service.STServiceLocator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

public final class ParameterTestHelper {

    /**
     * utility class
     */
    private ParameterTestHelper() {
        // do nothing
    }

    public static STParametre changeOrCreateParammeter(final CoreSession session, String name, String value) {
        STParametre param = STServiceLocator.getSTParametreService().getParametre(session, name);
        if (param != null) {
            param.setValue(value);
            DocumentModel doc = session.saveDocument(param.getDocument());
            return doc.getAdapter(STParametre.class);
        } else {
            return createParameter(session, name, value);
        }
    }

    /**
     * crée un parametre
     *
     * @param session
     * @param name
     * @param value
     * @return
     */
    private static STParametre createParameter(final CoreSession session, String name, String value) {
        DocumentModel doc = session.createDocumentModel("/", name, STConstant.PARAMETRE_DOCUMENT_TYPE);
        STParametre param = doc.getAdapter(STParametre.class);
        param.setValue(value);
        doc = session.createDocument(doc);
        ACP acp = doc.getACP();
        ACL acl = acp.getOrCreateACL(ACL.LOCAL_ACL);
        acl.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.READ));
        session.setACP(doc.getRef(), acp, true);
        return doc.getAdapter(STParametre.class);
    }
}
