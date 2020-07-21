package fr.dila.solonepp.webengine.wsepp;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.EtatEvenement;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;

/**
 * Test notifier verrou
 * 
 * @author Fabio Esposito 
 *
 */
public class TestNotifierVerrou extends AbstractEppWSTest {
    private static WSEpp wsEppGvt;

    private static WSEvenement wsEvenementGvt;

    @BeforeClass
    public static void setup() throws Exception {

        wsEppGvt = WSServiceHelper.getWSEppGvt();

        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
    }

    /**
     * Ce test vérifie la recherche de dossiers
     * 
     * @throws Exception
     */
    @WebTest(description = "Test vérifie la recherche de dossiers", useDriver = false)
    public void testNotifierVerrou() throws Exception {

        // GVT crée un dossier, un événement et une version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsepp/notifierVerrou/0010 Creer dossier EVT01 EFIM1100005G.xml";
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
        Assert.assertEquals(EtatEvenement.PUBLIE, evt01Reponse.getEtat());
        Assert.assertNotNull(evt01Reponse.getVersionCourante());
        Assert.assertNotNull(evt01Reponse.getHorodatage());

        // Récupère les données de l'événement créé pour les tests suivants
        final String idEvenement = evt01Reponse.getIdEvenement();

        // Notifier verrour par identifiant d'événement : verrouillage
        filename = "fr/dila/solonepp/webengine/wsepp/notifierVerrou/0020 Notifier Verrou VERROUILLER.xml";
        NotifierVerrouRequest notifierVerrouRequest = JaxBHelper.buildRequestFromFile(filename, NotifierVerrouRequest.class);
        Assert.assertNotNull(notifierVerrouRequest);
        notifierVerrouRequest.setIdEvenement(idEvenement);
        NotifierVerrouResponse notifierVerrouResponse = wsEppGvt.notifierVerrou(notifierVerrouRequest);
        Assert.assertNotNull(notifierVerrouResponse);
        traitementStatut = notifierVerrouResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierVerrouResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        String utilisateur = notifierVerrouResponse.getUtilisateur();
        Assert.assertNotNull(utilisateur);
        Assert.assertEquals("ws-gouvernement", utilisateur);

        // Notifier verrour par identifiant d'événement : verrouillage
        filename = "fr/dila/solonepp/webengine/wsepp/notifierVerrou/0030 Notifier Verrou DEVERROUILLER.xml";
        notifierVerrouRequest = JaxBHelper.buildRequestFromFile(filename, NotifierVerrouRequest.class);
        Assert.assertNotNull(notifierVerrouRequest);
        notifierVerrouRequest.setIdEvenement(idEvenement);
        notifierVerrouResponse = wsEppGvt.notifierVerrou(notifierVerrouRequest);
        Assert.assertNotNull(notifierVerrouResponse);
        traitementStatut = notifierVerrouResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(notifierVerrouResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);
        utilisateur = notifierVerrouResponse.getUtilisateur();
        Assert.assertNull(utilisateur);

    }
}
