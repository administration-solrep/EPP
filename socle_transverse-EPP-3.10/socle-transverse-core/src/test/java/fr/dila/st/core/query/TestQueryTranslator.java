package fr.dila.st.core.query;

import java.util.List;

import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.st.core.query.translation.QueryTranslator;
import fr.dila.st.core.query.translation.TranslatedStatement;

/**
 * Test de traduction de la clause where d'une requête SQL (et UFNXQL).
 * 
 * @author jgomez
 * 
 */
public class TestQueryTranslator extends SQLRepositoryTestCase {

	private QueryTranslator	translator;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		translator = new QueryTranslator();
	}

	/**
	 * Test toString de translatedStatement, normalement un test unitaire à part entière.
	 */
	public void testToStringTranslatedStatement() {
		TranslatedStatement statement = new TranslatedStatement();
		statement.setLogicalOperator("AND");
		statement.setSearchField("d.dos.nomRespDossier");
		statement.setConditionalOperator("=");
		statement.setValue("'ODAILLY'");
		assertEquals("d.dos.nomRespDossier = ODAILLY AND", statement.toString());
	}

	public void testTextSimpleUFNXQL() {
		String textClause = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = 'ODAILLY'";
		List<TranslatedStatement> results = translator.translateUFNXL(textClause);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("d.dos:nomRespDossier = ODAILLY", results.get(0).toString());
	}

	public void testVoc() {
		String vocExample = "SELECT * FROM Dossier AS dos WHERE d.dos:typeActe IN ('1','2','3')";
		List<TranslatedStatement> results = translator.translateUFNXL(vocExample);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("d.dos:typeActe IN 1,2,3", results.get(0).toString());
	}

	public void testBetweenDate() {
		String vocExample = "SELECT * FROM Dossier AS dos WHERE d.dos:datePublication BETWEEN  DATE '2011-04-04' AND DATE '2011-04-15'";
		List<TranslatedStatement> results = translator.translateUFNXL(vocExample);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals("d.dos:datePublication BETWEEN DATE 2011-04-04,DATE 2011-04-15", results.get(0).toString());
	}

	public void test2ClausesTextUFNXQL() {
		String textClause = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = 'ODAUVE' AND d.dos.prenomAuteur LIKE 'Albe%'";
		List<TranslatedStatement> results = translator.translateUFNXL(textClause);
		assertNotNull(results);
		assertEquals(2, results.size());
		assertEquals("d.dos:nomRespDossier = ODAUVE AND", results.get(0).toString());
		assertEquals("d.dos.prenomAuteur LIKE Albe%", results.get(1).toString());
	}

	public void testRealLifeUFNXQL() {
		String realLifeQuery = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\'"
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.dos:titreActe = \'xxx\' " + "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'";
		List<TranslatedStatement> results = translator.translateUFNXL(realLifeQuery);
		assertNotNull(results);
		assertEquals(5, results.size());
		assertEquals("d.dos:nomRespDossier = ODAILLY AND", results.get(0).toString());
		assertEquals("d.ecm:fulltext.dos:numeroNor LIKE RR*", results.get(4).toString());
		assertEquals("d.dos:categorieActe IN 2,1 AND", results.get(1).toString());
		assertEquals("d.dos:dateSignature BETWEEN DATE 2011-04-11,DATE 2011-04-29 AND", results.get(2).toString());
	}

	public void testSmallExampleUFNXQL() {
		String smallExample = "SELECT * FROM Dossier AS dos WHERE dos:idCreateurDossier = \'CPAROLINI\' AND ecm:fulltext.dos:numeroNor LIKE \'C*\'";
		List<TranslatedStatement> results = translator.translateUFNXL(smallExample);
		assertNotNull(results);
		assertEquals(2, results.size());
	}

}
