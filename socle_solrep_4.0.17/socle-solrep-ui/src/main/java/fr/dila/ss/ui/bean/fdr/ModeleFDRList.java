package fr.dila.ss.ui.bean.fdr;

import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class ModeleFDRList {
    protected List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<FeuilleRouteDTO> liste = new ArrayList<>();

    protected static final String COLUMN_ETAT = "etat";
    protected static final String COLUMN_INTITULE = "intitule";
    protected static final String COLUMN_AUTEUR = "auteur";
    protected static final String COLUMN_MINISTERE = "ministere";
    protected static final String COLUMN_DATE_MODIF = "derniereModif";

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    private boolean hasSelect;

    private boolean hasPagination;

    private boolean isSubstitutionTable;

    public ModeleFDRList() {
        this.nbTotal = 0;
    }

    public List<FeuilleRouteDTO> getListe() {
        return liste;
    }

    public void setListe(List<FeuilleRouteDTO> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public List<ColonneInfo> getListeColonnes() {
        return listeColonnes;
    }

    public List<ColonneInfo> getListeSortedColonnes() {
        return getListeColonnes()
            .stream()
            .filter(colonne -> colonne.getSortValue() != null && !colonne.getSortValue().isEmpty())
            .sorted((c1, c2) -> StringUtils.compare(c1.getSortOrder(), c2.getSortOrder(), false))
            .collect(Collectors.toList());
    }

    public List<ColonneInfo> getListeSortableAndVisibleColonnes() {
        return getListeColonnes()
            .stream()
            .filter(colonne -> colonne.isSortable() && colonne.isVisible())
            .collect(Collectors.toList());
    }

    public void buildColonnes(ModeleFDRListForm form) {
        listeColonnes.clear();

        if (form != null) {
            listeColonnes.add(
                new ColonneInfo(
                    "modeleFDR.content.header.etat",
                    true,
                    true,
                    COLUMN_ETAT,
                    form.getEtat(),
                    false,
                    true,
                    form.getEtatOrder()
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "modeleFDR.content.header.intitule",
                    true,
                    true,
                    COLUMN_INTITULE,
                    form.getIntitule(),
                    false,
                    true,
                    form.getIntituleOrder()
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "modeleFDR.content.header.ministere",
                    false,
                    COLUMN_MINISTERE,
                    form.getMinistere(),
                    true,
                    true
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "modeleFDR.content.header.auteur",
                    true,
                    true,
                    COLUMN_AUTEUR,
                    form.getAuteur(),
                    false,
                    true,
                    form.getAuteurOrder()
                )
            );
            listeColonnes.add(
                new ColonneInfo(
                    "modeleFDR.content.header.derniereModif",
                    true,
                    true,
                    COLUMN_DATE_MODIF,
                    form.getDerniereModif(),
                    true,
                    true,
                    form.getDerniereModifOrder()
                )
            );
            // Colonne lock
            listeColonnes.add(new ColonneInfo("header.label.lock", false, true, false, false));
            // Colonne actions
            listeColonnes.add(new ColonneInfo("header.label.actions", false, true, false, false));
        }
    }

    public void buildColonnesSubstitution(ModeleFDRListForm form) {
        listeColonnes.clear();

        listeColonnes.add(
            new ColonneInfo(
                "modeleFDR.content.header.intitule",
                true,
                true,
                COLUMN_INTITULE,
                form.getIntitule(),
                false,
                true,
                form.getIntituleOrder()
            )
        );
        listeColonnes.add(
            new ColonneInfo(
                "modeleFDR.content.header.auteur",
                true,
                true,
                COLUMN_AUTEUR,
                form.getAuteur(),
                false,
                true,
                form.getAuteurOrder()
            )
        );
        listeColonnes.add(
            new ColonneInfo(
                "modeleFDR.content.header.derniereModif",
                true,
                true,
                COLUMN_DATE_MODIF,
                form.getDerniereModif(),
                false,
                true,
                form.getDerniereModifOrder()
            )
        );
    }

    public void buildColonnesNonTriables() {
        listeColonnes.clear();
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.etat", false, true, false, true));
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.intitule", false, true, false, true));
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.ministere", false, true, false, true));
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.auteur", false, true, false, true));
        listeColonnes.add(new ColonneInfo("modeleFDR.content.header.derniereModif", false, true, false, true));
        // Colonne lock
        listeColonnes.add(new ColonneInfo("header.label.lock", false, true, false, false));
        // Colonne actions
        listeColonnes.add(new ColonneInfo("header.label.actions", false, true, false, false));
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getSousTitre() {
        return sousTitre;
    }

    public void setSousTitre(String sousTitre) {
        this.sousTitre = sousTitre;
    }

    public boolean getHasSelect() {
        return hasSelect;
    }

    public void setHasSelect(boolean hasSelect) {
        this.hasSelect = hasSelect;
    }

    public boolean getHasPagination() {
        return hasPagination;
    }

    public void setHasPagination(boolean hasPagination) {
        this.hasPagination = hasPagination;
    }

    public boolean getIsSubstitutionTable() {
        return isSubstitutionTable;
    }

    public void setIsSubstitutionTable(boolean isSubstitutionTable) {
        this.isSubstitutionTable = isSubstitutionTable;
    }
}
