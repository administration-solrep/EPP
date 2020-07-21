package fr.dila.solonepp.webengine.wsevenement;

import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.junit.BeforeClass;

import com.google.common.collect.Sets;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.Action;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.EtatVersion;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.Version;

/**
 * Tests fonctionnels des créations de version initiales.
 * 
 * @author jtremeaux
 */
public class TestCreerVersionBrouillonSansPJ extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    /**
     * Test de création de version :
     * - Emetteur crée une version à l'état brouillon -> 0.1 BROUILLON ;
     * - Emetteur modifie la version brouillon ;
     * - Emetteur publie la version brouillon -> 1.0 PUBLIEE.
     * 
     * @throws Exception
     */
    @WebTest(description = "Test de création de version", useDriver = false)
    public void testCreerVersionBrouillon() throws Exception {

        // GVT crée un dossier + événement créateur + version brouillon 0.1
        final String filename = "fr/dila/solonepp/webengine/wsevenement/creerVersionBrouillonSansPJ/0010 Creer dossier EVT01 CCOZ1100009Z.xml";
        final CreerVersionRequest creerVersionRequest = JaxBHelper.buildRequestFromFile(filename, CreerVersionRequest.class);
        Assert.assertNotNull(creerVersionRequest);
        final CreerVersionResponse creerVersionResponse = wsEvenementGvt.creerVersion(creerVersionRequest);
        Assert.assertNotNull(creerVersionResponse);
        final TraitementStatut traitementStatut = creerVersionResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(creerVersionResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        Assert.assertNotNull(creerVersionResponse.getEvenement());
        final EppEvt01 evt01Reponse = creerVersionResponse.getEvenement().getEvt01();
        Assert.assertNotNull(evt01Reponse);
        Assert.assertNotNull(evt01Reponse.getIdEvenement());
        Assert.assertEquals(EtatEvenement.BROUILLON, evt01Reponse.getEtat());
        final Version versionCourante = evt01Reponse.getVersionCourante();
        Assert.assertNotNull(versionCourante);
        Assert.assertEquals(0, versionCourante.getMajeur());
        Assert.assertEquals(1, versionCourante.getMineur());
        Assert.assertNull(versionCourante.getDateAr());
        Assert.assertEquals(EtatVersion.BROUILLON, versionCourante.getEtat());
        Assert.assertNotNull(versionCourante.getHorodatage());
        Assert.assertEquals(Institution.GOUVERNEMENT, evt01Reponse.getEmetteur());
        Assert.assertEquals(Institution.ASSEMBLEE_NATIONALE, evt01Reponse.getDestinataire());
        Assert.assertTrue("Trop de destinaires en copie: 1 attendu. ", evt01Reponse.getCopie().size() == 1);
        Assert.assertNotNull(evt01Reponse.getHorodatage());
        Assert.assertEquals(Institution.SENAT, evt01Reponse.getCopie().get(0));
        final Set<Action> expectedActionSuivante = Sets.newHashSet(Action.MODIFIER, Action.PUBLIER, Action.SUPPRIMER, Action.TRANSMETTRE_MEL);
        Assert.assertTrue("Actions suivantes attendues : " + expectedActionSuivante + ", retournées : " + evt01Reponse.getActionSuivante(),
                CollectionUtils.isEqualCollection(expectedActionSuivante, evt01Reponse.getActionSuivante()));

        // Récupère les identifiants générés pour les tests suivants
        //        String idEvenement = evt01Reponse.getIdEvenement();
        //        String pieceJointeTexteId = evt01Reponse.getTexte().getIdInterneEpp();
        //        String pieceJointeExposeDesMotifsId = evt01Reponse.getExposeDesMotifs().getIdInterneEpp();
        //        String pieceJointeDecretPresentationId = evt01Reponse.getDecretPresentation().getIdInterneEpp();
        //        String pieceJointeLettrePmId = evt01Reponse.getLettrePm().getIdInterneEpp();

    }

}
