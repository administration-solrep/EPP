package fr.dila.ss.ui.bean.supervision;

import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class SupervisionUserList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();
    private List<SupervisionUserDTO> liste = new ArrayList<>();

    public SupervisionUserList() {
        super();
    }

    public List<SupervisionUserDTO> getListe() {
        return liste;
    }

    public void setListe(List<SupervisionUserDTO> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes(UsersListForm form) {
        buildColonnes(form);
        return listeColonnes;
    }

    private void buildColonnes(UsersListForm form) {
        listeColonnes.clear();
        listeColonnes.add(
            ColonneInfo
                .builder("supervision.table.header.nom", true, true, false, true)
                .sortName("nom")
                .sortId(getSortId("nom"))
                .sortValue(form == null ? null : form.getNom())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("supervision.table.header.prenom", true, true, false, true)
                .sortName("prenom")
                .sortId(getSortId("prenom"))
                .sortValue(form == null ? null : form.getPrenom())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("supervision.table.header.utilisateur", true, true, false, true)
                .sortName("utilisateur")
                .sortId(getSortId("utilisateur"))
                .sortValue(form == null ? null : form.getUtilisateur())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("supervision.table.header.date.connexion", true, true, false, true)
                .sortName("dateLastConnexion")
                .sortId(getSortId("dateLastConnexion"))
                .sortValue(form == null ? null : form.getDateLastConnexion())
                .build()
        );
    }

    public List<ColonneInfo> getListeSortedColonnes(UsersListForm form) {
        return getListeColonnes(form)
            .stream()
            .filter(colonne -> StringUtils.isNotEmpty(colonne.getSortValue()))
            .sorted((c1, c2) -> StringUtils.compare(c1.getSortOrder(), c2.getSortOrder(), false))
            .collect(Collectors.toList());
    }

    public List<ColonneInfo> getListeSortableAndVisibleColonnes(UsersListForm form) {
        return getListeColonnes(form)
            .stream()
            .filter(colonne -> colonne.isSortable() && colonne.isVisible())
            .collect(Collectors.toList());
    }

    private String getSortId(String columnName) {
        return columnName + "Header";
    }
}
