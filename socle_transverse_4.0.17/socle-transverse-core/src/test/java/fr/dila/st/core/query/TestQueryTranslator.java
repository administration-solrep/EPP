package fr.dila.st.core.query;

import fr.dila.st.core.query.translation.QueryTranslator;
import fr.dila.st.core.query.translation.TranslatedStatement;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test de traduction de la clause where d'une requête SQL (et UFNXQL).
 *
 * @author jgomez
 *
 */
public class TestQueryTranslator {
    private QueryTranslator translator = new QueryTranslator();

    /**
     * Test toString de translatedStatement, normalement un test unitaire à part entière.
     */
    @Test
    public void testToStringTranslatedStatement() {
        TranslatedStatement statement = new TranslatedStatement();
        statement.setLogicalOperator("AND");
        statement.setSearchField("d.dos.nomRespDossier");
        statement.setConditionalOperator("=");
        statement.setValue("'ODAILLY'");
        Assert.assertEquals("d.dos.nomRespDossier = ODAILLY AND", statement.toString());
    }

    @Test
    public void testTextSimpleUFNXQL() {
        String textClause = "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = 'ODAILLY'";
        List<TranslatedStatement> results = translator.translateUFNXL(textClause);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("d.dos:nomRespDossier = ODAILLY", results.get(0).toString());
    }

    @Test
    public void testVoc() {
        String vocExample = "SELECT * FROM Dossier AS dos WHERE d.dos:typeActe IN ('1','2','3')";
        List<TranslatedStatement> results = translator.translateUFNXL(vocExample);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("d.dos:typeActe IN 1,2,3", results.get(0).toString());
    }

    @Test
    public void testBetweenDate() {
        String vocExample =
            "SELECT * FROM Dossier AS dos WHERE d.dos:datePublication BETWEEN  DATE '2011-04-04' AND DATE '2011-04-15'";
        List<TranslatedStatement> results = translator.translateUFNXL(vocExample);
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("d.dos:datePublication BETWEEN DATE 2011-04-04,DATE 2011-04-15", results.get(0).toString());
    }

    @Test
    public void test2ClausesTextUFNXQL() {
        String textClause =
            "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = 'ODAUVE' AND d.dos.prenomAuteur LIKE 'Albe%'";
        List<TranslatedStatement> results = translator.translateUFNXL(textClause);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("d.dos:nomRespDossier = ODAUVE AND", results.get(0).toString());
        Assert.assertEquals("d.dos.prenomAuteur LIKE Albe%", results.get(1).toString());
    }

    @Test
    public void testRealLifeUFNXQL() {
        String realLifeQuery =
            "SELECT * FROM Dossier AS d WHERE d.dos:nomRespDossier = \'ODAILLY\'" +
            "AND d.dos:categorieActe IN (\'2\',\'1\') " +
            "AND d.dos:dateSignature BETWEEN DATE \'2011-04-11\' AND DATE \'2011-04-29\' " +
            "AND d.dos:titreActe = \'xxx\' " +
            "AND d.ecm:fulltext.dos:numeroNor LIKE \'RR*\'";
        List<TranslatedStatement> results = translator.translateUFNXL(realLifeQuery);
        Assert.assertNotNull(results);
        Assert.assertEquals(5, results.size());
        Assert.assertEquals("d.dos:nomRespDossier = ODAILLY AND", results.get(0).toString());
        Assert.assertEquals("d.ecm:fulltext.dos:numeroNor LIKE RR*", results.get(4).toString());
        Assert.assertEquals("d.dos:categorieActe IN 2,1 AND", results.get(1).toString());
        Assert.assertEquals(
            "d.dos:dateSignature BETWEEN DATE 2011-04-11,DATE 2011-04-29 AND",
            results.get(2).toString()
        );
    }

    @Test
    public void testSmallExampleUFNXQL() {
        String smallExample =
            "SELECT * FROM Dossier AS dos WHERE dos:idCreateurDossier = \'CPAROLINI\' AND ecm:fulltext.dos:numeroNor LIKE \'C*\'";
        List<TranslatedStatement> results = translator.translateUFNXL(smallExample);
        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
    }
}
