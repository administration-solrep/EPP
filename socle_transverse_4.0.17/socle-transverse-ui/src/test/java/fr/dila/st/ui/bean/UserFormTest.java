package fr.dila.st.ui.bean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.common.collect.Lists;
import fr.dila.st.ui.th.bean.UserForm;
import java.util.ArrayList;
import org.junit.Test;

public class UserFormTest {

    @Test
    public void testConstructor() {
        UserForm user = new UserForm();
        assertNull(user.getAdresse());
        assertNull(user.getCivilite());
        assertNull(user.getCodePostal());
        assertNull(user.getDateDebut());
        assertNull(user.getDateFin());
        assertEquals("", user.getFullNameIdentifier());
        assertNull(user.getMel());
        assertNull(user.getNom());
        assertNull(user.getPostes());
        assertNull(user.getPrenom());
        assertThat(user.getProfils()).isEmpty();
        assertNull(user.getTelephone());
        assertNull(user.getTemporaire());
        assertNull(user.getUtilisateur());
        assertNull(user.getVille());

        user = new UserForm("nom", "prenom", "utilisateur", "mel", "dateDebut");
        assertEquals("nom", user.getNom());
        assertEquals("prenom", user.getPrenom());
        assertEquals("utilisateur", user.getUtilisateur());
        assertEquals("mel", user.getMel());
        assertEquals("dateDebut", user.getDateDebut());
        assertNull(user.getAdresse());
        assertNull(user.getCivilite());
        assertNull(user.getCodePostal());
        assertNull(user.getDateFin());
        assertEquals("nom prenom utilisateur", user.getFullNameIdentifier());
        assertNull(user.getPostes());
        assertThat(user.getProfils()).isEmpty();
        assertNull(user.getTelephone());
        assertNull(user.getTemporaire());
        assertNull(user.getVille());
    }

    @Test
    public void testSetters() {
        UserForm user = new UserForm();
        assertNull(user.getAdresse());
        assertNull(user.getCivilite());
        assertNull(user.getCodePostal());
        assertNull(user.getDateDebut());
        assertNull(user.getDateFin());
        assertEquals("", user.getFullNameIdentifier());
        assertNull(user.getMel());
        assertNull(user.getNom());
        assertNull(user.getPostes());
        assertNull(user.getPrenom());
        assertThat(user.getProfils()).isEmpty();
        assertNull(user.getTelephone());
        assertNull(user.getTemporaire());
        assertNull(user.getUtilisateur());
        assertNull(user.getVille());
        user.setAdresse("adresse");

        assertEquals("adresse", user.getAdresse());
        user.setCivilite("civ");

        assertEquals("civ", user.getCivilite());
        user.setCodePostal("cp");

        assertEquals("cp", user.getCodePostal());
        user.setDateDebut("dateD");

        assertEquals("dateD", user.getDateDebut());
        user.setDateFin("dateF");

        assertEquals("dateF", user.getDateFin());
        user.setMel("mel");

        assertEquals("mel", user.getMel());
        user.setNom("nom");

        assertEquals("nom", user.getNom());
        ArrayList<String> lstPoste = Lists.newArrayList("profil1");

        user.setPostes(lstPoste);
        assertEquals(lstPoste, user.getPostes());
        user.setPrenom("prénom");

        assertEquals("prénom", user.getPrenom());
        ArrayList<String> lstProfil = Lists.newArrayList("profil1");

        user.setProfils(lstProfil);
        assertEquals(lstProfil, user.getProfils());
        user.setTelephone("tél");

        assertEquals("tél", user.getTelephone());
        user.setTemporaire("temp");

        assertEquals("temp", user.getTemporaire());
        user.setUtilisateur("user");

        assertEquals("user", user.getUtilisateur());
        user.setVille("ville");

        assertEquals("ville", user.getVille());
    }
}
