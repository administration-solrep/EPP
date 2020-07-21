package fr.dila.solonepp.core.metadonnees;

import java.util.Map;

import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.solonepp.api.descriptor.metadonnees.DefaultValue;
import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.PropertyDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.SolonEppRepositoryTestCase;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Test du service des metadonnees.
 * 
 * @author asatre
 */
public class TestMetaDonneesService extends SolonEppRepositoryTestCase {

	private static final String	VERSION				= "version";
	private static final String	OBJET				= "objet";
	private static final String	NIVEAU_LECTURE		= "niveauLecture";
	private static final String	SENAT				= "senat";
	private static final String	EVT02				= "EVT02";
	// private static final String IDTABLEREF = "idtableref";
	private static final String	MANDAT				= "mandat";

	private static final String	INT					= "int";
	private static final String	TITLE				= "title";
	private static final String	COAUTEUR			= "coauteur";
	private static final String	NIVEAU_LECTURE_TYPE	= "niveau_lecture";
	private static final String	HORODATAGE			= "horodatage";
	private static final String	DESTINATAIRE		= "destinataire";
	private static final String	EMETTEUR			= "emetteur";
	private static final String	DOSSIER				= "dossier";
	private static final String	TIMESTAMP			= "timestamp";
	private static final String	TEXT				= "text";
	private static final String	EVT01				= "EVT01";
	private static final String	DESTINATAIRE_COPIE	= "destinataireCopie";

	@Override
	public void setUp() throws Exception {
		super.setUp();
		deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/metadonnees-schema-framework.xml");
	}

	/**
	 * Test de recuperation des metadonnees d'un evenement.
	 * 
	 * @throws Exception
	 */
	public void testMetaDonneesEVT01() throws Exception {
		// ne test pas toute les propriétés, sert juste a verifier si le mapping marche
		final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
		MetaDonneesDescriptor metaDonneesDescriptor = metaDonneesService.getEvenementType(EVT01);
		assertEquals(metaDonneesDescriptor.getName(), EVT01);

		EvenementMetaDonneesDescriptor evt = metaDonneesDescriptor.getEvenement();
		Map<String, PropertyDescriptor> mapPropertyEvt = evt.getProperty();

		PropertyDescriptor titre = mapPropertyEvt.get(TITLE);
		assertEquals(titre.getType(), INT);
		assertTrue(titre.isModifiable());
		assertFalse(titre.isObligatoire());
		assertFalse(titre.isMultiValue());
		assertTrue(titre.isRenseignerEpp());
		assertFalse(titre.isFicheDossier());
		assertFalse(titre.isVisibility());
		assertEquals(titre.getListInstitutions().size(), 0);

		PropertyDescriptor dossier = mapPropertyEvt.get(DOSSIER);
		assertEquals(dossier.getType(), TEXT);
		assertTrue(dossier.isModifiable());
		assertFalse(dossier.isObligatoire());
		assertFalse(dossier.isMultiValue());
		assertTrue(dossier.isRenseignerEpp());
		assertFalse(dossier.isFicheDossier());
		assertFalse(dossier.isVisibility());
		assertEquals(dossier.getListInstitutions().size(), 0);

		DefaultValue defValD = dossier.getDefaultValue();
		assertEquals(defValD.getType(), OBJET);
		assertEquals(defValD.getSource(), SolonEppSchemaConstant.EVENEMENT_SCHEMA);
		assertEquals(defValD.getValue(), DOSSIER);

		PropertyDescriptor emetteur = mapPropertyEvt.get(EMETTEUR);
		assertEquals(emetteur.getType(), TEXT);
		assertFalse(emetteur.isModifiable());
		assertTrue(emetteur.isObligatoire());
		assertFalse(emetteur.isMultiValue());
		assertFalse(emetteur.isRenseignerEpp());
		assertTrue(emetteur.isFicheDossier());
		assertFalse(emetteur.isVisibility());
		assertEquals(emetteur.getListInstitutions().size(), 1);

		PropertyDescriptor destinataire = mapPropertyEvt.get(DESTINATAIRE);
		assertEquals(destinataire.getType(), TEXT);
		assertTrue(destinataire.isModifiable());
		assertTrue(destinataire.isObligatoire());
		assertFalse(destinataire.isMultiValue());
		assertFalse(destinataire.isRenseignerEpp());
		assertTrue(destinataire.isFicheDossier());
		assertFalse(destinataire.isVisibility());
		assertEquals(destinataire.getListInstitutions().size(), 2);

		PropertyDescriptor destinataireCopie = mapPropertyEvt.get(DESTINATAIRE_COPIE);
		assertEquals(destinataireCopie.getType(), TEXT);
		assertFalse(destinataireCopie.isModifiable());
		assertTrue(destinataireCopie.isObligatoire());
		assertFalse(destinataireCopie.isMultiValue());
		assertFalse(destinataireCopie.isRenseignerEpp());
		assertFalse(destinataireCopie.isFicheDossier());
		assertFalse(destinataireCopie.isVisibility());
		assertEquals(destinataireCopie.getListInstitutions().size(), 2);

		VersionMetaDonneesDescriptor version = metaDonneesDescriptor.getVersion();
		Map<String, PropertyDescriptor> mapPropertyVersion = version.getProperty();

		PropertyDescriptor horodatage = mapPropertyVersion.get(HORODATAGE);
		assertEquals(horodatage.getType(), TIMESTAMP);
		assertTrue(horodatage.isModifiable());
		assertFalse(horodatage.isObligatoire());
		assertFalse(horodatage.isMultiValue());
		assertTrue(horodatage.isRenseignerEpp());
		assertTrue(horodatage.isFicheDossier());
		assertFalse(horodatage.isVisibility());
		assertEquals(horodatage.getListInstitutions().size(), 0);

		PropertyDescriptor coauteur = mapPropertyVersion.get(COAUTEUR);
		assertEquals(coauteur.getType(), MANDAT);
		assertTrue(coauteur.isModifiable());
		assertTrue(coauteur.isObligatoire());
		assertTrue(coauteur.isMultiValue());
		assertFalse(coauteur.isRenseignerEpp());
		assertTrue(coauteur.isFicheDossier());
		assertFalse(coauteur.isVisibility());
		assertEquals(coauteur.getListInstitutions().size(), 0);

		PropertyDescriptor niveauLecture = mapPropertyVersion.get(NIVEAU_LECTURE);
		assertEquals(niveauLecture.getType(), NIVEAU_LECTURE_TYPE);
		assertTrue(niveauLecture.isModifiable());
		assertTrue(niveauLecture.isObligatoire());
		assertFalse(niveauLecture.isMultiValue());
		assertFalse(niveauLecture.isRenseignerEpp());
		assertTrue(niveauLecture.isFicheDossier());
		assertFalse(niveauLecture.isVisibility());
		assertEquals(niveauLecture.getListInstitutions().size(), 0);

		DefaultValue defaultValue = niveauLecture.getDefaultValue();
		assertEquals(VERSION, defaultValue.getSource());
		assertEquals("AN", defaultValue.getValue());
		assertEquals("string", defaultValue.getType());

		MetaDonneesDescriptor metaDonneesDescriptorEVT02 = metaDonneesService.getEvenementType(EVT02);
		assertEquals(metaDonneesDescriptorEVT02.getName(), EVT02);

		VersionMetaDonneesDescriptor versionEVT02 = metaDonneesDescriptorEVT02.getVersion();
		Map<String, PropertyDescriptor> mapPropertyVersionEVT02 = versionEVT02.getProperty();
		PropertyDescriptor senat = mapPropertyVersionEVT02.get(SENAT);
		assertEquals(senat.getType(), TEXT);
		assertTrue(senat.isModifiable());
		assertFalse(senat.isObligatoire());
		assertFalse(senat.isMultiValue());
		assertFalse(senat.isRenseignerEpp());
		assertTrue(senat.isFicheDossier());
		assertTrue(senat.isVisibility());
		assertEquals(senat.getListInstitutions().size(), 0);

	}

	/**
	 * Test de recuperation de la map des metadonnees d'un evenement de type EVT01.
	 * 
	 * @throws Exception
	 */
	public void testMetaDonneesMapEVT01() throws Exception {
		// ne test pas toute les propriétés, sert juste a verifier si le mapping marche
		final MetaDonneesService metaDonneesService = SolonEppServiceLocator.getMetaDonneesService();
		Map<String, PropertyDescriptor> mapProperty = metaDonneesService.getMapProperty(EVT01);

		assertTrue(!mapProperty.keySet().isEmpty());
		assertTrue(mapProperty.keySet().size() == 18);

		PropertyDescriptor uuid = mapProperty.get(TITLE);
		assertEquals(uuid.getType(), INT);
		assertTrue(uuid.isModifiable());
		assertFalse(uuid.isObligatoire());
		assertFalse(uuid.isMultiValue());
		assertTrue(uuid.isRenseignerEpp());
		assertFalse(uuid.isFicheDossier());
		assertFalse(uuid.isVisibility());

		PropertyDescriptor dossier = mapProperty.get(DOSSIER);
		assertEquals(dossier.getType(), TEXT);
		assertTrue(dossier.isModifiable());
		assertFalse(dossier.isObligatoire());
		assertFalse(dossier.isMultiValue());
		assertTrue(dossier.isRenseignerEpp());
		assertFalse(dossier.isFicheDossier());
		assertFalse(dossier.isVisibility());

		PropertyDescriptor emetteur = mapProperty.get(EMETTEUR);
		assertEquals(emetteur.getType(), TEXT);
		assertFalse(emetteur.isModifiable());
		assertTrue(emetteur.isObligatoire());
		assertFalse(emetteur.isMultiValue());
		assertFalse(emetteur.isRenseignerEpp());
		assertTrue(emetteur.isFicheDossier());
		assertFalse(emetteur.isVisibility());

		PropertyDescriptor destinataire = mapProperty.get(DESTINATAIRE);
		assertEquals(destinataire.getType(), TEXT);
		assertTrue(destinataire.isModifiable());
		assertTrue(destinataire.isObligatoire());
		assertFalse(destinataire.isMultiValue());
		assertFalse(destinataire.isRenseignerEpp());
		assertTrue(destinataire.isFicheDossier());
		assertFalse(destinataire.isVisibility());

		PropertyDescriptor horodatage = mapProperty.get(HORODATAGE);
		assertEquals(horodatage.getType(), TIMESTAMP);
		assertTrue(horodatage.isModifiable());
		assertFalse(horodatage.isObligatoire());
		assertFalse(horodatage.isMultiValue());
		assertTrue(horodatage.isRenseignerEpp());
		assertTrue(horodatage.isFicheDossier());
		assertFalse(horodatage.isVisibility());

		PropertyDescriptor coauteur = mapProperty.get(COAUTEUR);
		assertEquals(coauteur.getType(), MANDAT);
		assertTrue(coauteur.isModifiable());
		assertTrue(coauteur.isObligatoire());
		assertTrue(coauteur.isMultiValue());
		assertFalse(coauteur.isRenseignerEpp());
		assertTrue(coauteur.isFicheDossier());
		assertFalse(coauteur.isVisibility());

	}
}
