package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.domain.file.File;
import fr.dila.st.core.test.STFeature;
import java.io.IOException;
import javax.inject.Inject;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestFileAdapter { // extends SQLRepositoryTestCase {
    @Inject
    private CoreSession session;

    /**
     * Test les getter/setter de l'apdapter File
     *
     * @throws IOException
     */
    @Test
    public void testFileImpl() throws IOException {
        final String filename = "Spirou";
        final String mimeType = "mimetype";

        final Blob content = new StringBlob("un contenu");

        DocumentRef docRef;

        DocumentModel doc = session.createDocumentModel(STConstant.FILE_DOCUMENT_TYPE);
        Assert.assertNotNull(doc);

        doc = session.createDocument(doc);

        docRef = doc.getRef();

        File file = doc.getAdapter(File.class);
        Assert.assertNotNull(file);

        Assert.assertEquals(null, file.getContent());
        Assert.assertEquals(null, file.getFilename());
        Assert.assertEquals(null, file.getMimeType());

        file.setContent(content);
        file.setFilename(filename);
        file.setMimeType(mimeType);

        Assert.assertEquals(content, file.getContent());
        Assert.assertEquals(filename, file.getFilename());
        Assert.assertEquals(mimeType, file.getMimeType());

        // check persistance
        session.saveDocument(doc);
        session.save();

        content.setDigest(DigestUtils.md5Hex(content.getStream()));

        doc = session.getDocument(docRef);
        file = doc.getAdapter(File.class);

        Assert.assertEquals(filename, file.getFilename());
        Assert.assertEquals(mimeType, file.getMimeType());
        Assert.assertEquals(content.getLength(), file.getContent().getLength());
        Assert.assertEquals(content.getEncoding(), file.getContent().getEncoding());
        Assert.assertEquals(content.getFilename(), file.getContent().getFilename());
        Assert.assertEquals(content.getMimeType(), file.getContent().getMimeType());
        Assert.assertEquals(content.getDigest(), file.getContent().getDigest());

        // test setFilname
        final String newFilename = "Spiroute";
        file.setFilename(newFilename);
        session.saveDocument(doc);
        session.save();
        Assert.assertEquals(newFilename, file.getFilename());
    }
}
