package fr.dila.ss.ui.bean.actualites;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_EMISSION;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_HASPJ;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_OBJET;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.core.util.FullTextUtil;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;

public abstract class AbstractActualiteRechercheForm
    extends AbstractSortablePaginationForm
    implements ActualiteRechercheForm {
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DATE_EMISSION_DEBUT
    )
    @FormParam("dateEmissionDebut")
    private Calendar dateEmissionDebut;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DATE_EMISSION_FIN
    )
    @FormParam("dateEmissionFin")
    private Calendar dateEmissionFin;

    @FormParam("objet")
    private String objet;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_OBJET
    )
    private String objetPattern;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DANS_HISTORIQUE
    )
    @FormParam("archivee")
    protected Boolean archivee;

    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_HASPJ
    )
    @FormParam("hasPj")
    private Boolean hasPj;

    @FormParam(ActualitesList.COLUMN_SORT_DATE_EMISSION)
    private SortOrder dateEmissionSort;

    @FormParam(ActualitesList.COLUMN_SORT_OBJET)
    private SortOrder objetSort;

    @FormParam(ActualitesList.COLUMN_SORT_HASPJ)
    private SortOrder hasPjSort;

    protected AbstractActualiteRechercheForm(int size) {
        super(size);
        this.archivee = null;
    }

    @Override
    public Calendar getDateEmissionDebut() {
        return dateEmissionDebut;
    }

    @Override
    public void setDateEmissionDebut(Calendar dateEmissionDebut) {
        this.dateEmissionDebut = dateEmissionDebut;
    }

    @Override
    public Calendar getDateEmissionFin() {
        return dateEmissionFin;
    }

    @Override
    public void setDateEmissionFin(Calendar dateEmissionFin) {
        this.dateEmissionFin = dateEmissionFin;
    }

    @Override
    public String getObjet() {
        return objet;
    }

    @Override
    public void setObjet(String objet) {
        this.objet = objet;
        objetPattern = FullTextUtil.preparePrefixSearch(objet);
    }

    @Override
    public Boolean getArchivee() {
        return archivee;
    }

    @Override
    public Boolean getHasPj() {
        return hasPj;
    }

    @Override
    public void setHasPj(Boolean hasPj) {
        this.hasPj = hasPj;
    }

    @Override
    public SortOrder getDateEmissionSort() {
        return dateEmissionSort;
    }

    @Override
    public void setDateEmissionSort(SortOrder dateEmissionSort) {
        this.dateEmissionSort = dateEmissionSort;
    }

    @Override
    public SortOrder getObjetSort() {
        return objetSort;
    }

    @Override
    public void setObjetSort(SortOrder objetSort) {
        this.objetSort = objetSort;
    }

    @Override
    public SortOrder getHasPjSort() {
        return hasPjSort;
    }

    @Override
    public void setHasPjSort(SortOrder hasPjSort) {
        this.hasPjSort = hasPjSort;
    }

    @Override
    protected void setDefaultSort() {
        setDateEmissionSort(SortOrder.DESC);
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(ACTUALITE_PREFIX_XPATH_DATE_EMISSION, new FormSort(getDateEmissionSort()));
        map.put(ACTUALITE_PREFIX_XPATH_OBJET, new FormSort(getObjetSort()));
        map.put(ACTUALITE_PREFIX_XPATH_HASPJ, new FormSort(getHasPjSort()));
        return map;
    }
}
