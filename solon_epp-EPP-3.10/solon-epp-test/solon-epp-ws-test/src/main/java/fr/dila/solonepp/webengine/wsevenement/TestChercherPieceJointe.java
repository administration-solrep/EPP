package fr.dila.solonepp.webengine.wsevenement;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.ChercherPieceJointeRequest;
import fr.sword.xsd.solon.epp.ChercherPieceJointeResponse;
import fr.sword.xsd.solon.epp.CompressionFichier;
import fr.sword.xsd.solon.epp.ContenuFichier;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EvtId;
import fr.sword.xsd.solon.epp.PieceJointe;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * Tests fonctionnels de recherche des pieces jointes
 * 
 * @author feo
 */
public class TestChercherPieceJointe extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    /**
     * Ce test vérifie la recherche d'événements par identifiant d'événement.
     * - Emetteur crée des dossier, événements et versions ;
     * - Les institutions recherchent les événements par identifiants d'événement et numéros de version.
     * 
     * @throws Exception
     */
    @WebTest(description = "vérifie la recherche d'événements par identifiant d'événement", useDriver = false)
    public void testRechercherPieceJointe() throws Exception {
        // GVT crée un dossier, un événement et une version brouillon 0.1
        String filename = "fr/dila/solonepp/webengine/wsevenement/chercherPieceJointe/0010 Creer dossier EVT01 EFIM1100007Z.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertEquals(0, evt01Reponse.getVersionCourante().getMajeur());
        Assert.assertEquals(1, evt01Reponse.getVersionCourante().getMineur());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        final String idEvenement = evt01Reponse.getIdEvenement();

        // GVT recherche l'événement par identifiant technique d'événement
        filename = "fr/dila/solonepp/webengine/wsevenement/chercherPieceJointe/0020 Rechercher piece jointe par id_evenement.xml";
        final ChercherPieceJointeRequest chercherPieceJointeRequest = JaxBHelper.buildRequestFromFile(filename, ChercherPieceJointeRequest.class);
        Assert.assertNotNull(chercherPieceJointeRequest);

        final EvtId evtId = chercherPieceJointeRequest.getIdEvt();
        Assert.assertNotNull(evtId);
        evtId.setId(idEvenement);
        chercherPieceJointeRequest.setIdEvt(evtId);
        final ChercherPieceJointeResponse chercherPieceJointeResponse = wsEvenementGvt.chercherPieceJointe(chercherPieceJointeRequest);
        Assert.assertNotNull(chercherPieceJointeResponse);
        traitementStatut = chercherPieceJointeResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(chercherPieceJointeResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        final PieceJointe pj = chercherPieceJointeResponse.getPieceJointe();
        Assert.assertNotNull(pj);
        Assert.assertEquals(pj.getUrl(), "http://www.url1.fr");
        Assert.assertEquals(pj.getLibelle(), "Texte");
        Assert.assertEquals(pj.getType(), PieceJointeType.TEXTE);

        final List<ContenuFichier> contenuList = pj.getFichier();
        Assert.assertNotNull(contenuList);
        final ContenuFichier contenu = contenuList.get(0);
        Assert.assertNotNull(contenu);

        Assert.assertEquals(contenu.getNomFichier(), "test1.gif");
        Assert.assertEquals(contenu.getMimeType(), "image/gif");
        Assert.assertEquals(contenu.getCompression(), CompressionFichier.AUCUNE);
        Assert.assertNotNull(contenu.getContenu());

    }
}
