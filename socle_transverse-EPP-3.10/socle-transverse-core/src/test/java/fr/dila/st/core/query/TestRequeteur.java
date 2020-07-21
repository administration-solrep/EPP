package fr.dila.st.core.query;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.st.core.query.translation.TranslatedStatement;
import fr.dila.st.core.query.translation.TranslatedStatement.Converter;

/**
 * Test de traduction de la clause where d'une requête SQL (et UFNXQL).
 * 
 * @author jgomez
 * 
 */

public class TestRequeteur extends SQLRepositoryTestCase {

	private Requeteur	requeteur;

	public void setUp() throws Exception {
		super.setUp();
		String realLifeQuery = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.dos:titreActe = \'xxx\' " + "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'";
		requeteur = new Requeteur();
		requeteur.setQuery(realLifeQuery);
		requeteur.updateTranslation();
	}

	public void testInitRequeteur() {
		List<TranslatedStatement> results = requeteur.getStatements();
		assertNotNull(results);
		assertEquals(5, results.size());
		assertEquals("d.dos:nomRespDossier = ODAILLY AND", results.get(0).toString());
		assertEquals("d.ecm:fulltext.dos:numeroNor LIKE RR*", results.get(4).toString());
		assertEquals("d.dos:categorieActe IN 2,1 AND", results.get(1).toString());
		assertEquals("d.dos:dateSignature BETWEEN DATE 2011-04-11,DATE 2011-04-29 AND", results.get(2).toString());
	}

	public void testRemoveIndexSimple() {
		requeteur.remove(1);
		requeteur.remove(1);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:titreActe = \'xxx\' " + "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'",
				requeteur.getQuery());
	}

	public void testConverterValueList() {
		String listValueUserSide = "'2','1'";
		String expectedListValueTechnnicalSide = "('2','1')";
		TranslatedStatement st = new TranslatedStatement();
		st.setConditionalOperator("IN");
		Converter converter = new TranslatedStatement.ListConverter();
		assertEquals(expectedListValueTechnnicalSide, converter.convert(st, listValueUserSide));
		listValueUserSide = "'2'";
		expectedListValueTechnnicalSide = "('2')";
		assertEquals(expectedListValueTechnnicalSide, converter.convert(st, listValueUserSide));

	}

	public void testConverterDateList() {
		String dateValueUserSide = "DATE 2011-04-11,DATE 2011-04-29 ";
		String expectedValueTechnnicalSide = "DATE 2011-04-11 AND DATE 2011-04-29 ";
		TranslatedStatement st = new TranslatedStatement();
		Converter converter = new TranslatedStatement.DateConverter();
		assertEquals(expectedValueTechnnicalSide, converter.convert(st, dateValueUserSide));
	}

	public void testRemoveIndexWithList() {
		requeteur.remove(2);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') " + "AND d.dos:titreActe = \'xxx\' "
				+ "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'", requeteur.getQuery());
	}

	public void testRemoveIndexWithDate() {
		requeteur.remove(3);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'", requeteur.getQuery());
	}

	public void testUp() {
		requeteur.up(1);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:categorieActe IN (\'2\',\'1\') "
				+ "AND d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.dos:titreActe = \'xxx\' " + "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'",
				requeteur.getQuery());
	}

	public void testDown() {
		requeteur.down(1);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') " + "AND d.dos:titreActe = \'xxx\' "
				+ "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'", requeteur.getQuery());
	}

	public void testDownAndUpLimit() {
		requeteur.down(10);
		String realLifeQuery = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\' "
				+ "AND d.dos:categorieActe IN (\'2\',\'1\') "
				+ "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' "
				+ "AND d.dos:titreActe = \'xxx\' " + "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'";
		assertEquals(realLifeQuery, requeteur.getQuery());
		requeteur.down(5);
		assertEquals(realLifeQuery, requeteur.getQuery());
		requeteur.up(0);
		assertEquals(realLifeQuery, requeteur.getQuery());
		requeteur.up(3);
		requeteur.down(2);
		assertEquals(realLifeQuery, requeteur.getQuery());

	}

	public void testSwap() {
		String myQuery = "SELECT * FROM Question AS q WHERE q.qu:nomCompletAuteur = \'Jean\' AND q.qu:titreActe = \'Hello\'";
		requeteur = new Requeteur();
		requeteur.setQuery(myQuery);
		requeteur.updateTranslation();
		assertNotNull(requeteur.getStatements());
		assertEquals(2, requeteur.getStatements().size());
		assertTrue(StringUtils.isBlank(requeteur.getStatements().get(1).getLogicalOperator()));
		assertEquals("AND", requeteur.getStatements().get(0).getLogicalOperator());
		requeteur.swapStatement(0, 1);
		requeteur.buildFromTranslation();
		assertEquals(
				"SELECT * FROM Question AS q WHERE q.qu:titreActe = \'Hello\' AND q.qu:nomCompletAuteur = \'Jean\'",
				requeteur.getQuery());
		assertTrue(StringUtils.isBlank(requeteur.getStatements().get(1).getLogicalOperator()));
		assertEquals("AND", requeteur.getStatements().get(0).getLogicalOperator());
		requeteur.swapStatement(1, 0);
		requeteur.buildFromTranslation();
		assertTrue(StringUtils.isBlank(requeteur.getStatements().get(1).getLogicalOperator()));
		assertEquals("AND", requeteur.getStatements().get(0).getLogicalOperator());
		assertEquals(myQuery, requeteur.getQuery());
		requeteur.swapStatement(1, 0);
		requeteur.buildFromTranslation();
		assertEquals(
				"SELECT * FROM Question AS q WHERE q.qu:titreActe = \'Hello\' AND q.qu:nomCompletAuteur = \'Jean\'",
				requeteur.getQuery());
		assertTrue(StringUtils.isBlank(requeteur.getStatements().get(1).getLogicalOperator()));
		assertEquals("AND", requeteur.getStatements().get(0).getLogicalOperator());
	}

	public void test2ClausesProblem() {
		String myQuery = "SELECT * FROM Question AS q WHERE q.qu:nomCompletAuteur = \'Jean\' AND q.qu:titreActe = \'Hello\'";
		requeteur = new Requeteur();
		requeteur.setQuery(myQuery);
		requeteur.updateTranslation();
		requeteur.up(1);
		assertEquals(
				"SELECT * FROM Question AS q WHERE q.qu:titreActe = \'Hello\' AND q.qu:nomCompletAuteur = \'Jean\'",
				requeteur.getQuery());
		requeteur.down(0);
		assertEquals(myQuery, requeteur.getQuery());
	}

	public void testEmptyClause() {
		String query = "SELECT * FROM Dossier AS d WHERE";
		requeteur = new Requeteur();
		requeteur.setQuery(query);
		requeteur.updateTranslation();
		assertNotNull(requeteur.getStatements());
		assertEquals(0, requeteur.getStatements().size());
		assertEquals(query, requeteur.getQuery());
		requeteur.remove(0);
	}

	public void testRemoveOneOfTwoClauses() {
		String query = "SELECT * FROM Dossier AS d WHERE d.dos:titreActe = \'Hello Jack\' AND d.dos:texteDossier = \'Bye bye Jack\'";
		requeteur = new Requeteur();
		requeteur.setQuery(query);
		requeteur.updateTranslation();
		assertNotNull(requeteur.getStatements());
		assertEquals(2, requeteur.getStatements().size());
		assertEquals(query, requeteur.getQuery());
		requeteur.remove(0);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:texteDossier = \'Bye bye Jack\'", requeteur.getQuery());
		requeteur = new Requeteur();
		requeteur.setQuery(query);
		requeteur.updateTranslation();
		requeteur.remove(1);
		assertEquals("SELECT * FROM Dossier AS d WHERE d.dos:titreActe = \'Hello Jack\'", requeteur.getQuery());
	}

	public void testBugRealLife() {
		String maRequete = "SELECT * FROM Dossier AS d WHERE q.ecm:fulltext_idQuestion = \"4562\" AND q.qu:typeQuestion IN (\"QE\")";
		requeteur = new Requeteur();
		requeteur.setQuery(maRequete);
		requeteur.updateTranslation();
		requeteur.up(1);
		assertEquals(
				"SELECT * FROM Dossier AS d WHERE q.qu:typeQuestion IN (\'QE\') AND q.ecm:fulltext_idQuestion = \'4562\'",
				requeteur.getQuery());
	}

}
