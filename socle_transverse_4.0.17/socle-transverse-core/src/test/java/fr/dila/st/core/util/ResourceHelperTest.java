package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STLogFactory.class)
@PowerMockIgnore("javax.management.*")
public class ResourceHelperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private STLogger logger;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STLogFactory.class);

        when(STLogFactory.getLog(ResourceHelper.class)).thenReturn(logger);
    }

    @Test
    public void testGetStringWithKey() {
        // Vérification que la clé surchargée est correcte
        assertThat(ResourceHelper.getString("cle")).isEqualTo("appli");

        // Vérification qu'on accède aux clés non surchargées
        assertThat(ResourceHelper.getString("cle1")).isEqualTo("socle transverse");
        assertThat(ResourceHelper.getString("cle2")).isEqualTo("socle solrep");
        assertThat(ResourceHelper.getString("cle3")).isEqualTo("appli");

        // test avec les quotes
        assertThat(ResourceHelper.getString("cle.simple.quote")).isEqualTo("message de test avec lapostrophe");
        assertThat(ResourceHelper.getString("cle.double.quote")).isEqualTo("message de test avec l'apostrophe doublée");
        assertThat(ResourceHelper.getString("cle.triple.quote")).isEqualTo("message de test avec l'apostrophe triplée");

        // test avec paramètre
        assertThat(ResourceHelper.getString("cle.avec.parametres")).isEqualTo("params : {0} {1} {2}");

        // test avec des accolades
        assertThat(ResourceHelper.getString("cle.avec.accolades")).isEqualTo("message avec des accolades {test}");
    }

    @Test
    public void testGetStringWithKeyAndArguments() {
        // Given
        String key = "cle.avec.parametres";
        String key2 = "cle.avec.parametres2";
        String keyWithoutParameters = "cle";

        // When
        String rawMessageWithoutParameters = ResourceHelper.getString(keyWithoutParameters, "param");

        String rawMessage = ResourceHelper.getString(key);
        String messageWith1Parameter = ResourceHelper.getString(key, "param1");
        String messageWith2Parameters = ResourceHelper.getString(key, "param1", "param2");
        String messageWith3Parameters = ResourceHelper.getString(key, "param1", "param2", "param3");
        String messageWith4Parameters = ResourceHelper.getString(key, "param1", "param2", "param3", "param4");
        String messageWith1QuotedParameter = ResourceHelper.getString(key2, "param1");

        // Then
        assertThat(rawMessageWithoutParameters).isEqualTo("appli");

        assertThat(rawMessage).isEqualTo("params : {0} {1} {2}");
        assertThat(messageWith1Parameter).isEqualTo("params : param1 {1} {2}");
        assertThat(messageWith2Parameters).isEqualTo("params : param1 param2 {2}");
        assertThat(messageWith3Parameters).isEqualTo("params : param1 param2 param3");
        assertThat(messageWith4Parameters).isEqualTo("params : param1 param2 param3");
        assertThat(messageWith1QuotedParameter).isEqualTo("params : \"param1\"");
    }

    @Test
    public void testGetStringWithKeyAndQuotesAndArguments() {
        // Given
        String key1 = "cle.avec.parametre.simple.quote";
        String key2 = "cle.avec.parametre.double.quote";
        String key3 = "cle.avec.parametre.triple.quote";

        String message1 = ResourceHelper.getString(key1, "test de l'apostrophe");
        String message2 = ResourceHelper.getString(key2, "test de l''apostrophe");
        String message3 = ResourceHelper.getString(key2, "test de l'apostrophe");
        String message4 = ResourceHelper.getString(key3, "test");

        assertThat(message1).isEqualTo("paramètre avec une apostrophe  : {0}");
        assertThat(message2).isEqualTo("paramètre avec 2 apostrophes ' : test de l''apostrophe");
        assertThat(message3).isEqualTo("paramètre avec 2 apostrophes ' : test de l'apostrophe");
        assertThat(message4).isEqualTo("paramètre avec 3 apostrophes ' : {0}");
    }

    @Test
    public void testGetStringWithUnknownKey() {
        String unknownKey = "unknown_key";

        assertThat(ResourceHelper.getString(unknownKey)).isEqualTo(unknownKey);
    }

    @Test
    public void testGetStringWithKeys() {
        Map<String, String> labelKeys = ImmutableMap.of(
            "INDEX_COMPL",
            "indexmode.compl",
            "INDEX_ORIG",
            "indexmode.orig",
            "TOUS",
            "indexmode.tous"
        );

        Map<String, String> labels = ResourceHelper.getStrings(labelKeys);

        assertThat(labels)
            .containsExactly(
                entry("INDEX_COMPL", "Indexation Ministères"),
                entry("INDEX_ORIG", "Indexation Parlement"),
                entry("TOUS", "Tout")
            );
    }

    @Test
    public void testTranslateKeysInStringWithEmptyKey() {
        assertThat(ResourceHelper.translateKeysInString("", " ")).isEqualTo("");

        verifyZeroInteractions(logger);
    }

    @Test
    public void testTranslateKeysInString() {
        String key1 = "pdf.cle1";
        String keys1 = "pdf.cle2" + " " + "pdf.cle4";
        String keys2 = "pdf.cle2" + " " + "pdf.cle4" + " " + "pdf.cle3";

        String messageWithOneKey = ResourceHelper.translateKeysInString(key1, " ");
        String messageWithKeys1 = ResourceHelper.translateKeysInString(keys1, " ");
        String messageWithKeys2 = ResourceHelper.translateKeysInString(keys2, " ");

        assertThat(messageWithOneKey).isEqualTo("Donne avis Favorable Dossier");
        assertThat(messageWithKeys1).isEqualTo("Donne avis Défavorable Dossier Pour attribution");
        assertThat(messageWithKeys2).isEqualTo("Donne avis Défavorable Dossier Pour attribution Pour visa");
    }
}
