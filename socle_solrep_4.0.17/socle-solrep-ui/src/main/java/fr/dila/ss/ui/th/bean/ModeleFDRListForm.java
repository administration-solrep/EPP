package fr.dila.ss.ui.th.bean;

import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

@SwBean(keepdefaultValue = true)
public class ModeleFDRListForm extends AbstractSortablePaginationForm {
    @QueryParam("etat")
    @FormParam("etat")
    private SortOrder etat;

    @QueryParam("etatOrder")
    @FormParam("etatOrder")
    private Integer etatOrder;

    @QueryParam("intitule")
    @FormParam("intitule")
    private SortOrder intitule;

    @QueryParam("intituleOrder")
    @FormParam("intituleOrder")
    private Integer intituleOrder;

    @QueryParam("ministere")
    @FormParam("ministere")
    private SortOrder ministere;

    @QueryParam("auteur")
    @FormParam("auteur")
    private SortOrder auteur;

    @QueryParam("auteurOrder")
    @FormParam("auteurOrder")
    private Integer auteurOrder;

    @QueryParam("derniereModif")
    @FormParam("derniereModif")
    private SortOrder derniereModif;

    @QueryParam("derniereModifOrder")
    @FormParam("derniereModifOrder")
    private Integer derniereModifOrder;

    @QueryParam("searchIntitule")
    @FormParam("searchIntitule")
    private String searchIntitule;

    @QueryParam("numero")
    @FormParam("numero")
    private SortOrder numero;

    @QueryParam("numeroOrder")
    @FormParam("numeroOrder")
    private Integer numeroOrder;

    public ModeleFDRListForm() {
        super();
    }

    public SortOrder getEtat() {
        return etat;
    }

    public void setEtat(SortOrder etat) {
        this.etat = etat;
    }

    public Integer getEtatOrder() {
        return etatOrder;
    }

    public void setEtatOrder(Integer etatOrder) {
        this.etatOrder = etatOrder;
    }

    public SortOrder getIntitule() {
        return intitule;
    }

    public void setIntitule(SortOrder intitule) {
        this.intitule = intitule;
    }

    public Integer getIntituleOrder() {
        return intituleOrder;
    }

    public void setIntituleOrder(Integer intituleOrder) {
        this.intituleOrder = intituleOrder;
    }

    public SortOrder getMinistere() {
        return ministere;
    }

    public void setMinistere(SortOrder ministere) {
        this.ministere = ministere;
    }

    public SortOrder getAuteur() {
        return auteur;
    }

    public void setAuteur(SortOrder auteur) {
        this.auteur = auteur;
    }

    public Integer getAuteurOrder() {
        return auteurOrder;
    }

    public void setAuteurOrder(Integer auteurOrder) {
        this.auteurOrder = auteurOrder;
    }

    public SortOrder getDerniereModif() {
        return derniereModif;
    }

    public void setDerniereModif(SortOrder derniereModif) {
        this.derniereModif = derniereModif;
    }

    public Integer getDerniereModifOrder() {
        return derniereModifOrder;
    }

    public void setDerniereModifOrder(Integer derniereModifOrder) {
        this.derniereModifOrder = derniereModifOrder;
    }

    public String getSearchIntitule() {
        return searchIntitule;
    }

    public void setSearchIntitule(String searchIntitule) {
        this.searchIntitule = searchIntitule;
    }

    public SortOrder getNumero() {
        return numero;
    }

    public void setNumero(SortOrder numero) {
        this.numero = numero;
    }

    public Integer getNumeroOrder() {
        return numeroOrder;
    }

    public void setNumeroOrder(Integer numeroOrder) {
        this.numeroOrder = numeroOrder;
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(STSchemaConstant.ECM_CURRENT_LIFE_CYCLE_STATE_XPATH, new FormSort(etat, etatOrder));
        map.put(SSFeuilleRouteConstant.FEUILLE_ROUTE_DEMANDE_VALIDATION_XPATH, new FormSort(etat, etatOrder));
        map.put(STSchemaConstant.DUBLINCORE_TITLE_XPATH, new FormSort(intitule, intituleOrder));
        map.put(STSchemaConstant.DUBLINCORE_CREATOR_XPATH, new FormSort(auteur, auteurOrder));
        map.put(STSchemaConstant.DUBLINCORE_MODIFIED_XPATH, new FormSort(derniereModif, derniereModifOrder));
        map.put(SSFeuilleRouteConstant.FEUILLE_ROUTE_NUMERO_XPATH, new FormSort(numero, numeroOrder));

        return map;
    }

    @Override
    protected void setDefaultSort() {
        intitule = SortOrder.ASC;
    }

    public static ModeleFDRListForm newForm() {
        return initForm(new ModeleFDRListForm());
    }
}
