package fr.dila.solonepp.core.metadonnees;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.SolonEppFeature;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.descriptor.parlement.DefaultValue;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service des metadonnees.
 *
 * @author asatre
 */
@RunWith(FeaturesRunner.class)
@Features(SolonEppFeature.class)
public class TestMetaDonneesService {
    private static final String VERSION = "version";
    private static final String OBJET = "objet";
    private static final String NIVEAU_LECTURE = "niveauLecture";
    private static final String SENAT = "senat";
    private static final String EVT02 = "EVT02";
    // private static final String IDTABLEREF = "idtableref";
    private static final String MANDAT = "mandat";

    private static final String INT = "int";
    private static final String TITLE = "title";
    private static final String COAUTEUR = "coauteur";
    private static final String NIVEAU_LECTURE_TYPE = "niveau_lecture";
    private static final String HORODATAGE = "horodatage";
    private static final String DESTINATAIRE = "destinataire";
    private static final String EMETTEUR = "emetteur";
    private static final String DOSSIER = "dossier";
    private static final String TIMESTAMP = "timestamp";
    private static final String TEXT = "text";
    private static final String EVT01 = "EVT01";
    private static final String DESTINATAIRE_COPIE = "destinataireCopie";

    /**
     * Test de recuperation des metadonnees d'un evenement.
     *
     * @throws Exception
     */
    @Test
    public void testMetaDonneesEVT01() throws Exception {
        // ne test pas toute les propriétés, sert juste a verifier si le mapping marche
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(EVT01);
        Assert.assertEquals(metaDonneesDescriptor.getName(), EVT01);

        EvenementMetaDonneesDescriptor evt = metaDonneesDescriptor.getEvenement();
        Map<String, PropertyDescriptor> mapPropertyEvt = evt.getProperty();

        PropertyDescriptor titre = mapPropertyEvt.get(TITLE);
        Assert.assertEquals(titre.getType(), INT);
        Assert.assertTrue(titre.isModifiable());
        Assert.assertFalse(titre.isObligatoire());
        Assert.assertFalse(titre.isMultiValue());
        Assert.assertTrue(titre.isRenseignerEpp());
        Assert.assertFalse(titre.isFicheDossier());
        Assert.assertFalse(titre.isVisibility());
        Assert.assertEquals(titre.getListInstitutions().size(), 0);

        PropertyDescriptor dossier = mapPropertyEvt.get(DOSSIER);
        Assert.assertEquals(dossier.getType(), TEXT);
        Assert.assertTrue(dossier.isModifiable());
        Assert.assertFalse(dossier.isObligatoire());
        Assert.assertFalse(dossier.isMultiValue());
        Assert.assertTrue(dossier.isRenseignerEpp());
        Assert.assertFalse(dossier.isFicheDossier());
        Assert.assertFalse(dossier.isVisibility());
        Assert.assertEquals(dossier.getListInstitutions().size(), 0);

        DefaultValue defValD = dossier.getDefaultValue();
        Assert.assertEquals(defValD.getType(), OBJET);
        Assert.assertEquals(defValD.getSource(), SolonEppSchemaConstant.EVENEMENT_SCHEMA);
        Assert.assertEquals(defValD.getValue(), DOSSIER);

        PropertyDescriptor emetteur = mapPropertyEvt.get(EMETTEUR);
        Assert.assertEquals(emetteur.getType(), TEXT);
        Assert.assertFalse(emetteur.isModifiable());
        Assert.assertTrue(emetteur.isObligatoire());
        Assert.assertFalse(emetteur.isMultiValue());
        Assert.assertFalse(emetteur.isRenseignerEpp());
        Assert.assertTrue(emetteur.isFicheDossier());
        Assert.assertFalse(emetteur.isVisibility());
        Assert.assertEquals(emetteur.getListInstitutions().size(), 1);

        PropertyDescriptor destinataire = mapPropertyEvt.get(DESTINATAIRE);
        Assert.assertEquals(destinataire.getType(), TEXT);
        Assert.assertTrue(destinataire.isModifiable());
        Assert.assertTrue(destinataire.isObligatoire());
        Assert.assertFalse(destinataire.isMultiValue());
        Assert.assertFalse(destinataire.isRenseignerEpp());
        Assert.assertTrue(destinataire.isFicheDossier());
        Assert.assertFalse(destinataire.isVisibility());
        Assert.assertEquals(destinataire.getListInstitutions().size(), 2);

        PropertyDescriptor destinataireCopie = mapPropertyEvt.get(DESTINATAIRE_COPIE);
        Assert.assertEquals(destinataireCopie.getType(), TEXT);
        Assert.assertFalse(destinataireCopie.isModifiable());
        Assert.assertTrue(destinataireCopie.isObligatoire());
        Assert.assertFalse(destinataireCopie.isMultiValue());
        Assert.assertFalse(destinataireCopie.isRenseignerEpp());
        Assert.assertFalse(destinataireCopie.isFicheDossier());
        Assert.assertFalse(destinataireCopie.isVisibility());
        Assert.assertEquals(destinataireCopie.getListInstitutions().size(), 2);

        VersionMetaDonneesDescriptor version = metaDonneesDescriptor.getVersion();
        Map<String, PropertyDescriptor> mapPropertyVersion = version.getProperty();

        PropertyDescriptor horodatage = mapPropertyVersion.get(HORODATAGE);
        Assert.assertEquals(horodatage.getType(), TIMESTAMP);
        Assert.assertTrue(horodatage.isModifiable());
        Assert.assertFalse(horodatage.isObligatoire());
        Assert.assertFalse(horodatage.isMultiValue());
        Assert.assertTrue(horodatage.isRenseignerEpp());
        Assert.assertTrue(horodatage.isFicheDossier());
        Assert.assertFalse(horodatage.isVisibility());
        Assert.assertEquals(horodatage.getListInstitutions().size(), 0);

        PropertyDescriptor coauteur = mapPropertyVersion.get(COAUTEUR);
        Assert.assertEquals(coauteur.getType(), MANDAT);
        Assert.assertTrue(coauteur.isModifiable());
        Assert.assertTrue(coauteur.isObligatoire());
        Assert.assertTrue(coauteur.isMultiValue());
        Assert.assertFalse(coauteur.isRenseignerEpp());
        Assert.assertTrue(coauteur.isFicheDossier());
        Assert.assertFalse(coauteur.isVisibility());
        Assert.assertEquals(coauteur.getListInstitutions().size(), 0);

        PropertyDescriptor niveauLecture = mapPropertyVersion.get(NIVEAU_LECTURE);
        Assert.assertEquals(niveauLecture.getType(), NIVEAU_LECTURE_TYPE);
        Assert.assertTrue(niveauLecture.isModifiable());
        Assert.assertTrue(niveauLecture.isObligatoire());
        Assert.assertFalse(niveauLecture.isMultiValue());
        Assert.assertFalse(niveauLecture.isRenseignerEpp());
        Assert.assertTrue(niveauLecture.isFicheDossier());
        Assert.assertFalse(niveauLecture.isVisibility());
        Assert.assertEquals(niveauLecture.getListInstitutions().size(), 0);

        DefaultValue defaultValue = niveauLecture.getDefaultValue();
        Assert.assertEquals(VERSION, defaultValue.getSource());
        Assert.assertEquals("AN", defaultValue.getValue());
        Assert.assertEquals("string", defaultValue.getType());

        MetaDonneesDescriptor metaDonneesDescriptorEVT02 = metaDonneesService.getEvenementType(EVT02);
        Assert.assertEquals(metaDonneesDescriptorEVT02.getName(), EVT02);

        VersionMetaDonneesDescriptor versionEVT02 = metaDonneesDescriptorEVT02.getVersion();
        Map<String, PropertyDescriptor> mapPropertyVersionEVT02 = versionEVT02.getProperty();
        PropertyDescriptor senat = mapPropertyVersionEVT02.get(SENAT);
        Assert.assertEquals(senat.getType(), TEXT);
        Assert.assertTrue(senat.isModifiable());
        Assert.assertFalse(senat.isObligatoire());
        Assert.assertFalse(senat.isMultiValue());
        Assert.assertFalse(senat.isRenseignerEpp());
        Assert.assertTrue(senat.isFicheDossier());
        Assert.assertTrue(senat.isVisibility());
        Assert.assertEquals(senat.getListInstitutions().size(), 0);
    }

    /**
     * Test de recuperation de la map des metadonnees d'un evenement de type EVT01.
     *
     * @throws Exception
     */
    @Test
    public void testMetaDonneesMapEVT01() throws Exception {
        // ne test pas toute les propriétés, sert juste a verifier si le mapping marche
        final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
        Map<String, PropertyDescriptor> mapProperty = metaDonneesService.getMapProperty(EVT01);

        Assert.assertTrue(!mapProperty.keySet().isEmpty());
        Assert.assertTrue(mapProperty.keySet().size() == 18);

        PropertyDescriptor uuid = mapProperty.get(TITLE);
        Assert.assertEquals(uuid.getType(), INT);
        Assert.assertTrue(uuid.isModifiable());
        Assert.assertFalse(uuid.isObligatoire());
        Assert.assertFalse(uuid.isMultiValue());
        Assert.assertTrue(uuid.isRenseignerEpp());
        Assert.assertFalse(uuid.isFicheDossier());
        Assert.assertFalse(uuid.isVisibility());

        PropertyDescriptor dossier = mapProperty.get(DOSSIER);
        Assert.assertEquals(dossier.getType(), TEXT);
        Assert.assertTrue(dossier.isModifiable());
        Assert.assertFalse(dossier.isObligatoire());
        Assert.assertFalse(dossier.isMultiValue());
        Assert.assertTrue(dossier.isRenseignerEpp());
        Assert.assertFalse(dossier.isFicheDossier());
        Assert.assertFalse(dossier.isVisibility());

        PropertyDescriptor emetteur = mapProperty.get(EMETTEUR);
        Assert.assertEquals(emetteur.getType(), TEXT);
        Assert.assertFalse(emetteur.isModifiable());
        Assert.assertTrue(emetteur.isObligatoire());
        Assert.assertFalse(emetteur.isMultiValue());
        Assert.assertFalse(emetteur.isRenseignerEpp());
        Assert.assertTrue(emetteur.isFicheDossier());
        Assert.assertFalse(emetteur.isVisibility());

        PropertyDescriptor destinataire = mapProperty.get(DESTINATAIRE);
        Assert.assertEquals(destinataire.getType(), TEXT);
        Assert.assertTrue(destinataire.isModifiable());
        Assert.assertTrue(destinataire.isObligatoire());
        Assert.assertFalse(destinataire.isMultiValue());
        Assert.assertFalse(destinataire.isRenseignerEpp());
        Assert.assertTrue(destinataire.isFicheDossier());
        Assert.assertFalse(destinataire.isVisibility());

        PropertyDescriptor horodatage = mapProperty.get(HORODATAGE);
        Assert.assertEquals(horodatage.getType(), TIMESTAMP);
        Assert.assertTrue(horodatage.isModifiable());
        Assert.assertFalse(horodatage.isObligatoire());
        Assert.assertFalse(horodatage.isMultiValue());
        Assert.assertTrue(horodatage.isRenseignerEpp());
        Assert.assertTrue(horodatage.isFicheDossier());
        Assert.assertFalse(horodatage.isVisibility());

        PropertyDescriptor coauteur = mapProperty.get(COAUTEUR);
        Assert.assertEquals(coauteur.getType(), MANDAT);
        Assert.assertTrue(coauteur.isModifiable());
        Assert.assertTrue(coauteur.isObligatoire());
        Assert.assertTrue(coauteur.isMultiValue());
        Assert.assertFalse(coauteur.isRenseignerEpp());
        Assert.assertTrue(coauteur.isFicheDossier());
        Assert.assertFalse(coauteur.isVisibility());
    }
}
