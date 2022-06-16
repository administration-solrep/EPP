package fr.dila.st.ui.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fr.dila.st.core.test.STCommonFeature;
import fr.dila.st.core.util.CoreSessionUtil;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.NxProp.Way;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STCommonFeature.class)
public class MapDoc2BeanIT {

    public static class MyBean {
        @NxProp(xpath = "dc:title", docType = "File")
        private String title;

        @NxProp(xpath = "ecm:uuid", docType = "File")
        private String uuid;

        @NxProp(xpath = "ecm:primaryType", docType = "File")
        private String type;

        @NxProp(xpath = "ecm:currentLifeCycleState", docType = "File")
        private String state;

        @NxProp(
            xpath = "dc:creator",
            docType = "File",
            process = MapDoc2BeanProcessFormatUsername.class,
            way = Way.DOC_TO_BEAN
        )
        private String userFullName;

        @NxProp(xpath = "ecm:currentLifeCycleState", docType = "Folder")
        private String stateFolder;
    }

    @Inject
    private CoreSession session;

    @Before
    public void setup() {}

    @Test
    public void test2Bean() {
        DocumentModel doc = initDoc(session);

        MyBean bean = new MyBean();
        bean.title = "titre";

        MapDoc2Bean.beanToDoc(bean, doc);

        assertEquals("titre", doc.getPropertyValue("dc:title"));
        assertEquals("Administrator", doc.getPropertyValue("dc:creator"));

        MyBean extracted = MapDoc2Bean.docToBean(doc, MyBean.class);
        assertEquals("titre", extracted.title);
        assertEquals(doc.getId(), extracted.uuid);
        assertEquals("File", extracted.type);
        assertEquals("project", extracted.state);
        assertEquals("Administrator", extracted.userFullName);
        assertNull(extracted.stateFolder);

        DocumentModel docFolder = initFolderDoc(session);
        extracted = MapDoc2Bean.docToBean(docFolder, extracted);
        assertEquals("project", extracted.stateFolder);
    }

    @Test
    public void testMapDoc2BeanProcessFormatUsername() {
        try (CloseableCoreSession userSession = CoreSessionUtil.openSession(session, "user1")) {
            DocumentModel doc = initDoc(userSession);
            MyBean extracted = MapDoc2Bean.docToBean(doc, MyBean.class);
            assertEquals("user1f user1l", extracted.userFullName);
        }
    }

    private DocumentModel initDoc(CoreSession session) {
        DocumentModel doc = session.createDocumentModel("File");
        return session.createDocument(doc);
    }

    private DocumentModel initFolderDoc(CoreSession session) {
        DocumentModel doc = session.createDocumentModel("Folder");
        return session.createDocument(doc);
    }
}
