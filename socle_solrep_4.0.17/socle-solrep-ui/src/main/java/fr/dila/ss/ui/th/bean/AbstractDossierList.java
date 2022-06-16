package fr.dila.ss.ui.th.bean;

import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.IColonneInfo;
import fr.dila.st.ui.enums.SortOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractDossierList {
    private final List<IColonneInfo> listeColonnes = new ArrayList<>();

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public AbstractDossierList(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public void setNbTotal(long nbTotal) {
        this.nbTotal = (int) nbTotal;
    }

    public List<IColonneInfo> getListeColonnes() {
        return listeColonnes;
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

    public List<IColonneInfo> getListeSortedColonnes() {
        return getListeColonnes()
            .stream()
            .filter(colonne -> colonne.getSortValue() != null && !colonne.getSortValue().isEmpty())
            .sorted((c1, c2) -> StringUtils.compare(c1.getSortOrder(), c2.getSortOrder(), false))
            .collect(Collectors.toList());
    }

    public List<IColonneInfo> getListeSortableAndVisibleColonnes() {
        return getListeColonnes()
            .stream()
            .filter(colonne -> colonne.isSortable() && colonne.isVisible())
            .collect(Collectors.toList());
    }

    public ColonneInfo buildColonne(
        String label,
        String userColumn,
        List<String> lstUserVisibleColumns,
        boolean labelVisible
    ) {
        return buildColonne(label, userColumn, lstUserVisibleColumns, false, null, null, labelVisible, null);
    }

    public ColonneInfo buildColonne(
        String label,
        String userColumn,
        List<String> lstUserVisibleColumns,
        boolean sortable,
        String name,
        SortOrder value,
        Boolean isLabelVisible,
        Integer order
    ) {
        boolean userPrefSet = lstUserVisibleColumns != null;
        boolean visible = false;

        if (userColumn == null || userPrefSet && lstUserVisibleColumns.contains(userColumn)) {
            visible = true;
        }

        return new ColonneInfo(label, sortable, visible, name, value, false, isLabelVisible, order);
    }
}
