package fr.dila.st.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.core.requete.recherchechamp.RechercheChampService;
import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import fr.dila.st.core.test.STFeature;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
@Deploy("fr.dila.st.core:OSGI-INF/test-recherche-champ-contrib.xml")
@BlacklistComponent("fr.dila.st.core.datasources.contrib")
public class RechercheChampServiceIT {
    @Inject
    private RechercheChampService service;

    private static final String CONTRIB_NAME = "fr.dila.st.core.test.rechercheChampContrib";

    @Test
    public void testGetService() {
        assertThat(service).isNotNull();
    }

    @Test
    public void testGetChamp() {
        // test service.getChamp
        ChampDescriptor dateCaducite = service.getChamp(CONTRIB_NAME, "date_caducite");
        assertThat(dateCaducite).isNotNull();
        assertThat(dateCaducite.getName()).isEqualTo("date_caducite");
        assertThat(dateCaducite.getLabel()).isEqualTo("label.requeteur.question_condition_date_caducite");
        assertThat(dateCaducite.getTypeChamp()).isEqualTo("dates");
        assertThat(dateCaducite.getField()).isEqualTo("q.qu:dateCaduciteQuestion");
        assertThat(dateCaducite.getParametres()).isEmpty();
        assertThat(dateCaducite.getChampParameterKlass()).isNull();

        // test champ.getParametres
        ChampDescriptor auteur = service.getChamp(CONTRIB_NAME, "auteur");
        assertThat(auteur).isNotNull();
        assertThat(auteur.getParametres()).hasSize(1);
        Parametre param = auteur.getParametres().get(0);
        assertThat(param.getName()).isEqualTo("autocomplete");
        assertThat(param.getValue()).isEqualTo("auteur");
    }

    @Test
    public void testAddAdditionalParamsToChamp() {
        ChampDescriptor etatEtape = service.getChamp(CONTRIB_NAME, "etat_etape");
        assertThat(etatEtape).isNotNull();
        assertThat(etatEtape.getChampParameterKlass()).isNotNull();
        List<Parametre> params = etatEtape.getParametres();
        assertThat(params).hasSize(2);
        assertThat(params.get(0).getName()).isEqualTo("param1");
        assertThat(params.get(0).getValue()).isEqualTo("val1");
        assertThat(params.get(1).getName()).isEqualTo("param2");
        assertThat(params.get(1).getValue()).isEqualTo("val2");
    }

    @Test
    public void testGetChamps() {
        assertThat(service.getChamps(CONTRIB_NAME)).hasSize(3);
    }
}
