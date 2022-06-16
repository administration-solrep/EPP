package fr.dila.ss.ui.th.bean;

import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_CATEGORY;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_COMMENT;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_PATH;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_DOC_UUID;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_EVENT_DATE;
import static org.nuxeo.ecm.platform.audit.api.BuiltinLogEntryData.LOG_PRINCIPAL_NAME;

import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class JournalDossierForm extends AbstractSortablePaginationForm {
    public static final String DATE_PARAM = "date";
    public static final String USER_PARAM = "user";
    public static final String POSTE_PARAM = "userPoste";
    public static final String CATEGORY_PARAM = "category";
    public static final String COMMENT_PARAM = "comment";
    public static final String REF_DOSSIER_PARAM = "refDossier";

    @QueryParam(DATE_PARAM)
    @FormParam(DATE_PARAM)
    private SortOrder date;

    @QueryParam(USER_PARAM)
    @FormParam(USER_PARAM)
    private SortOrder utilisateur;

    @QueryParam(POSTE_PARAM)
    @FormParam(POSTE_PARAM)
    private SortOrder poste;

    @QueryParam(CATEGORY_PARAM)
    @FormParam(CATEGORY_PARAM)
    private SortOrder categorie;

    @QueryParam(COMMENT_PARAM)
    @FormParam(COMMENT_PARAM)
    private SortOrder commentaire;

    @QueryParam(REF_DOSSIER_PARAM)
    @FormParam(REF_DOSSIER_PARAM)
    private SortOrder referenceDossier;

    public SortOrder getDate() {
        return date;
    }

    public void setDate(SortOrder date) {
        this.date = date;
    }

    public SortOrder getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(SortOrder utilisateur) {
        this.utilisateur = utilisateur;
    }

    public SortOrder getPoste() {
        return poste;
    }

    public void setPoste(SortOrder poste) {
        this.poste = poste;
    }

    public SortOrder getCategorie() {
        return categorie;
    }

    public void setCategorie(SortOrder categorie) {
        this.categorie = categorie;
    }

    public SortOrder getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(SortOrder commentaire) {
        this.commentaire = commentaire;
    }

    public SortOrder getReferenceDossier() {
        return referenceDossier;
    }

    public void setReferenceDossier(SortOrder referenceDossier) {
        this.referenceDossier = referenceDossier;
    }

    @Override
    protected void setDefaultSort() {
        // no default sort
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(LOG_EVENT_DATE, new FormSort(date));
        map.put(LOG_PRINCIPAL_NAME, new FormSort(utilisateur));
        map.put(LOG_DOC_PATH, new FormSort(poste));
        map.put(LOG_CATEGORY, new FormSort(categorie));
        map.put(LOG_COMMENT, new FormSort(commentaire));
        map.put(LOG_DOC_UUID, new FormSort(referenceDossier));
        return map;
    }
}
