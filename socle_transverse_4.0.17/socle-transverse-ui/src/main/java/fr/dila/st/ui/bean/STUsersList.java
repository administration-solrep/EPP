package fr.dila.st.ui.bean;

import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import java.util.ArrayList;
import java.util.List;

public class STUsersList {
    protected final List<ColonneInfo> listeColonnes = new ArrayList<>();
    protected final boolean haveSortableColumns;
    protected List<String> lstLettres = new ArrayList<>();
    protected List<UserForm> liste = new ArrayList<>();

    protected Integer nbTotal;

    public STUsersList() {
        this(true);
    }

    public STUsersList(boolean haveSortableColumns) {
        this.haveSortableColumns = haveSortableColumns;
        buildColonnes(null);
    }

    public List<UserForm> getListe() {
        return liste;
    }

    public void setListe(List<UserForm> liste) {
        this.liste = liste;
    }

    public List<ColonneInfo> getListeColonnes() {
        return getListeColonnes(null);
    }

    public List<ColonneInfo> getListeColonnes(UsersListForm form) {
        buildColonnes(form);
        return listeColonnes;
    }

    protected void buildColonnes(UsersListForm form) {
        listeColonnes.clear();
        if (form != null && haveSortableColumns) {
            listeColonnes.add(new ColonneInfo("users.column.header.nom", true, UsersListForm.NOM_TRI, form.getNom()));
            listeColonnes.add(
                new ColonneInfo("users.column.header.prenom", true, UsersListForm.PRENOM_TRI, form.getPrenom())
            );
            listeColonnes.add(
                new ColonneInfo(
                    "users.column.header.utilisateur",
                    true,
                    UsersListForm.UTILISATEUR_TRI,
                    form.getUtilisateur()
                )
            );
            listeColonnes.add(
                    new ColonneInfo(
                            "users.column.header.ministeres",
                            true,
                            UsersListForm.MINISTERES_TRI,
                            form.getMinisteres()
                    )
            );
            listeColonnes.add(new ColonneInfo("users.column.header.mel", true, UsersListForm.MEL_TRI, form.getMel()));
            listeColonnes.add(
                new ColonneInfo(
                    "users.column.header.dateDebut",
                    true,
                    UsersListForm.DATE_DEBUT_TRI,
                    form.getDateDebut()
                )
            );
        } else {
            listeColonnes.add(new ColonneInfo("users.column.header.nom", false, true, false, true));
            listeColonnes.add(new ColonneInfo("users.column.header.prenom", false, true, false, true));
            listeColonnes.add(new ColonneInfo("users.column.header.utilisateur", false, true, false, true));
            listeColonnes.add(new ColonneInfo("users.column.header.ministeres", false, true, false, true));
            listeColonnes.add(new ColonneInfo("users.column.header.mel", false, true, false, true));
            listeColonnes.add(new ColonneInfo("users.column.header.dateDebut", false, true, false, true));
        }


    }

    public List<String> getLstLettres() {
        return lstLettres;
    }

    public void setLstLettres(List<String> lstLettres) {
        this.lstLettres = lstLettres;
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
}
