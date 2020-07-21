package fr.dila.solonepp.webengine.wsevenement;

import junit.framework.Assert;

import org.junit.BeforeClass;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.solonepp.webengine.base.AbstractEppWSTest;
import fr.dila.solonepp.webengine.helper.WSServiceHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.naiad.commons.webtest.annotation.WebTest;
import fr.sword.xsd.commons.TraitementStatut;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EppEvt01;
import fr.sword.xsd.solon.epp.MajInterneRequest;
import fr.sword.xsd.solon.epp.MajInterneResponse;

/**
 * Tests fonctionnels de la mise a jour du champ interne d'un message
 * 
 * @author asatre
 */
public class TestMajVisaInterne extends AbstractEppWSTest {

    private static WSEvenement wsEvenementGvt;

    private static WSEvenement wsEvenementAn;

    @BeforeClass
    public static void setup() throws Exception {
        wsEvenementGvt = WSServiceHelper.getWSEvenementGvt();
        wsEvenementAn = WSServiceHelper.getWSEvenementAn();

    }

    @WebTest(description = "MajVisaInterne", useDriver = false)
    public void testMajVisaInterne() throws Exception {

        // AN crée un dossier + événement créateur + version publiée 1.0
        String filename = "fr/dila/solonepp/webengine/wsevenement/majVisaInterne/0010 Creer dossier EVT01 CCOZ1200011L.xml";
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

        final String idEvenenement = evt01Reponse.getIdEvenement();
        // majVisa AUCUN/0
        filename = "fr/dila/solonepp/webengine/wsevenement/majVisaInterne/0020 MajVisaInterneRequest CCOZ1200011L.xml";
        MajInterneRequest majVisaInterneRequest = JaxBHelper.buildRequestFromFile(filename, MajInterneRequest.class);
        Assert.assertNotNull(majVisaInterneRequest);
        majVisaInterneRequest.setIdEvenement(idEvenenement);

        MajInterneResponse majVisaInterneResponse = wsEvenementAn.majInterne(majVisaInterneRequest);
        Assert.assertNotNull(majVisaInterneRequest);

        traitementStatut = majVisaInterneResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(majVisaInterneResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

        // majVisa PARTIEL/1
        filename = "fr/dila/solonepp/webengine/wsevenement/majVisaInterne/0030 MajVisaInterneRequest CCOZ1200011L.xml";
        majVisaInterneRequest = JaxBHelper.buildRequestFromFile(filename, MajInterneRequest.class);
        Assert.assertNotNull(majVisaInterneRequest);
        majVisaInterneRequest.setIdEvenement(idEvenenement);

        majVisaInterneResponse = wsEvenementAn.majInterne(majVisaInterneRequest);
        Assert.assertNotNull(majVisaInterneRequest);

        traitementStatut = majVisaInterneResponse.getStatut();
        Assert.assertNotNull(traitementStatut);
        Assert.assertEquals(majVisaInterneResponse.getMessageErreur(), TraitementStatut.OK, traitementStatut);

    }
}
