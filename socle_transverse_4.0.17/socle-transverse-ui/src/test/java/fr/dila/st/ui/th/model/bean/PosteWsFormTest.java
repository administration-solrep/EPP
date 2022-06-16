package fr.dila.st.ui.th.model.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fr.dila.st.ui.th.bean.PosteWsForm;
import java.util.ArrayList;
import org.junit.Test;

public class PosteWsFormTest {

    @Test
    public void testDTO() throws Exception {
        String id = "identifiant";
        String libelle = "libelle";
        String dateDebut = "07/08/20020";
        String urlWs = "urlwebservice";
        String utilisateurWs = "utilisateur web service";
        String mdpWs = "Motdepassewebservice";
        String keystore = "Nom de la clé dans le keystore";
        String ministere1 = "ministere1";
        String ministere2 = "ministere2";

        PosteWsForm form = new PosteWsForm();
        assertNotNull(form);
        assertNull(form.getLibelle());
        assertNull(form.getUrlWs());
        assertNull(form.getUtilisateurWs());
        assertNull(form.getMdpWs());
        assertNull(form.getKeystore());

        form.setId(id);
        form.setLibelle(libelle);
        form.setDateDebut(dateDebut);
        form.setUrlWs(urlWs);
        form.setUtilisateurWs(utilisateurWs);
        form.setMdpWs(mdpWs);
        form.setKeystore(keystore);

        ArrayList<String> ministeres = new ArrayList<>();
        ministeres.add(ministere1);
        ministeres.add(ministere2);
        form.setMinisteres(ministeres);

        assertEquals(id, form.getId());
        assertEquals(libelle, form.getLibelle());
        assertEquals(dateDebut, form.getDateDebut());
        assertFalse(form.getMinisteres().isEmpty());
        assertTrue(form.getMinisteres().size() == 2);
        assertTrue(form.getMinisteres().get(0).equals(ministere1));
        assertTrue(form.getMinisteres().get(1).equals(ministere2));
        assertEquals(urlWs, form.getUrlWs());
        assertEquals(utilisateurWs, form.getUtilisateurWs());
        assertEquals(mdpWs, form.getMdpWs());
        assertEquals(keystore, form.getKeystore());
    }
}
