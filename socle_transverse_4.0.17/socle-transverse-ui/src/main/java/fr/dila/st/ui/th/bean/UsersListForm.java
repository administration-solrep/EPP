package fr.dila.st.ui.th.bean;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.nuxeo.ecm.core.api.SortInfo;

import static fr.dila.st.api.constant.STSchemaConstant.*;

@SwBean(keepdefaultValue = true)
public class UsersListForm extends AbstractSortablePaginationForm {
    public static final String NOM_TRI = "nomTri";
    public static final String PRENOM_TRI = "prenomTri";
    public static final String UTILISATEUR_TRI = "utilisateurTri";
    public static final String MEL_TRI = "melTri";
    public static final String DATE_DEBUT_TRI = "dateDebutTri";
    public static final String DATE_LAST_CONNEXION_TRI = "dateLastConnexionTri";
    public static final String MINISTERES_TRI = "ministeresTri";

    private static final List<Triple<Predicate<UsersListForm>, String, Function<UsersListForm, SortOrder>>> SORT_COLUMNS = ImmutableList.of(
        ImmutableTriple.of(form -> form.getNom() != null, USER_LAST_NAME, UsersListForm::getNom),
        ImmutableTriple.of(form -> form.getPrenom() != null, USER_FIRST_NAME, UsersListForm::getPrenom),
        ImmutableTriple.of(form -> form.getUtilisateur() != null, USER_USERNAME, UsersListForm::getUtilisateur),
        ImmutableTriple.of(form -> form.getMel() != null, USER_EMAIL, UsersListForm::getMel),
        ImmutableTriple.of(form -> form.getDateDebut() != null, USER_DATE_DEBUT, UsersListForm::getDateDebut),
        ImmutableTriple.of(
            form -> form.getDateLastConnexion() != null,
            USER_DATE_DERNIERE_CONNEXION,
            UsersListForm::getDateLastConnexion
        )
    );

    @QueryParam("recherche")
    private String recherche;

    @QueryParam("nom")
    @FormParam(NOM_TRI)
    protected SortOrder nom;

    @QueryParam("prenom")
    @FormParam(PRENOM_TRI)
    protected SortOrder prenom;

    @QueryParam("utilisateur")
    @FormParam(UTILISATEUR_TRI)
    protected SortOrder utilisateur;

    @QueryParam("mel")
    @FormParam(MEL_TRI)
    private SortOrder mel;

    @QueryParam("dateDebut")
    @FormParam(DATE_DEBUT_TRI)
    private SortOrder dateDebut;

    @QueryParam("dateLastConnexion")
    @FormParam(DATE_LAST_CONNEXION_TRI)
    protected SortOrder dateLastConnexion;

    @QueryParam("ministeres")
    @FormParam(MINISTERES_TRI)
    protected SortOrder ministeres;

    public UsersListForm() {
        super();
    }

    public SortOrder getMinisteres() {
        return ministeres;
    }

    public void setMinisteres(SortOrder ministeres) {
        this.ministeres = ministeres;
    }

    public SortOrder getNom() {
        return nom;
    }

    public void setNom(SortOrder nom) {
        this.nom = nom;
    }

    public SortOrder getPrenom() {
        return prenom;
    }

    public void setPrenom(SortOrder prenom) {
        this.prenom = prenom;
    }

    public SortOrder getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(SortOrder utilisateur) {
        this.utilisateur = utilisateur;
    }

    public SortOrder getMel() {
        return mel;
    }

    public void setMel(SortOrder mel) {
        this.mel = mel;
    }

    public SortOrder getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(SortOrder dateDebut) {
        this.dateDebut = dateDebut;
    }

    public String getRecherche() {
        return recherche;
    }

    public void setRecherche(String recherche) {
        this.recherche = Optional.ofNullable(recherche).map(r -> r.replace("*", "")).orElse(null);
    }

    public SortOrder getDateLastConnexion() {
        return dateLastConnexion;
    }

    public void setDateLastConnexion(SortOrder dateLastConnexion) {
        this.dateLastConnexion = dateLastConnexion;
    }

    @Override
    protected void setDefaultSort() {
        nom = SortOrder.ASC;
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(STSchemaConstant.USER_LAST_NAME, new FormSort(getNom()));
        map.put(STSchemaConstant.USER_FIRST_NAME, new FormSort(getPrenom()));
        map.put(STSchemaConstant.USER_USERNAME, new FormSort(getUtilisateur()));
        map.put(STSchemaConstant.USER_EMAIL, new FormSort(getMel()));
        map.put(STSchemaConstant.USER_DATE_DEBUT, new FormSort(getDateDebut()));
        //        map.put(STSchemaConstant.USER_USERNAME_XPATH, new FormSort(getDateLastConnexion()));
        return map;
    }

    public Pair<String, Boolean> getSortColumn() {
        return SORT_COLUMNS
            .stream()
            .filter(column -> column.getLeft().test(this))
            .map(triple -> ImmutablePair.of(triple.getMiddle(), SortOrder.isAscending(triple.getRight().apply(this))))
            .findFirst()
            .orElse(ImmutablePair.of(USER_LAST_NAME, true));
    }

    public List<SortInfo> getSortInfosForRequeteur() {
        Pair<String, Boolean> sortColumnn = getSortColumn();
        return Collections.singletonList(new SortInfo(sortColumnn.getKey(), sortColumnn.getValue()));
    }
}
