package fr.dila.ss.ui.bean.actualites;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_VALIDITE;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Calendar;
import java.util.Map;
import javax.ws.rs.FormParam;

@SwBean(keepdefaultValue = true)
public class ActualiteAdminRechercheForm extends AbstractActualiteRechercheForm {
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DATE_VALIDITE_DEBUT
    )
    @FormParam("dateFinValiditeDebut")
    private Calendar dateFinValiditeDebut;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DATE_VALIDITE_FIN
    )
    @FormParam("dateFinValiditeFin")
    private Calendar dateFinValiditeFin;

    @FormParam(ActualitesList.COLUMN_SORT_DATE_FIN_VALIDITE)
    private SortOrder dateFinValiditeSort;

    @FormParam(ActualitesList.COLUMN_SORT_STATUS)
    private SortOrder statusSort;

    public ActualiteAdminRechercheForm() {
        super(SIZE_20);
    }

    public Calendar getDateFinValiditeDebut() {
        return dateFinValiditeDebut;
    }

    public void setDateFinValiditeDebut(Calendar dateFinValiditeDebut) {
        this.dateFinValiditeDebut = dateFinValiditeDebut;
    }

    public Calendar getDateFinValiditeFin() {
        return dateFinValiditeFin;
    }

    public void setDateFinValiditeFin(Calendar dateFinValiditeFin) {
        this.dateFinValiditeFin = dateFinValiditeFin;
    }

    public SortOrder getDateFinValiditeSort() {
        return dateFinValiditeSort;
    }

    public void setDateFinValiditeSort(SortOrder dateFinValiditeSort) {
        this.dateFinValiditeSort = dateFinValiditeSort;
    }

    public SortOrder getStatusSort() {
        return statusSort;
    }

    public void setStatusSort(SortOrder statusSort) {
        this.statusSort = statusSort;
    }

    public void setArchivee(Boolean archivee) {
        this.archivee = archivee;
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = super.getSortForm();
        map.put(ACTUALITE_PREFIX_XPATH_DATE_VALIDITE, new FormSort(dateFinValiditeSort));
        map.put(ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE, new FormSort(statusSort));
        return map;
    }
}
