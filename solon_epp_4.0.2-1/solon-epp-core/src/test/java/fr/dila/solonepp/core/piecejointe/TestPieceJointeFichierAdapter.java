package fr.dila.solonepp.core.piecejointe;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.piecejointe.PieceJointeFichier;
import fr.dila.solonepp.core.SolonEppFeature;
import java.io.IOException;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
public class TestPieceJointeFichierAdapter {
    @Inject
    private CoreSession session;

    /**
     * Test les getter/setter de l'apdapter {@link PieceJointeFichier}
     *
     * @throws IOException
     */
    @Test
    public void testPieceJointeFichierImpl() throws IOException {
        final String title = "maPiece";
        final String digest = "digest";

        DocumentModel doc = session.createDocumentModel(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE);
        Assert.assertNotNull(doc);

        doc = session.createDocument(doc);

        String id = doc.getId();

        PieceJointeFichier file = doc.getAdapter(PieceJointeFichier.class);
        Assert.assertNotNull(file);

        Assert.assertEquals(null, file.getTitle());
        Assert.assertEquals(null, file.getDigestSha512());

        file.setTitle(title);
        // dans le cas réel, cette valeur est mise en place par l'appelant,
        // soit l'interface (FileTreeManagerActionsBean) soit le service (PieceJointeAssembler)
        file.setDigestSha512(digest);

        Assert.assertEquals(title, file.getTitle());
        Assert.assertEquals(digest, file.getDigestSha512());

        // check persistance
        session.saveDocument(doc);
        session.save();

        doc = session.getDocument(new IdRef(id));
        file = doc.getAdapter(PieceJointeFichier.class);

        Assert.assertEquals(title, file.getTitle());
        Assert.assertEquals(digest, file.getDigestSha512());
    }
}
