package fr.dila.ss.ui.bean;

import static fr.dila.ss.ui.th.bean.JournalDossierForm.CATEGORY_PARAM;
import static fr.dila.ss.ui.th.bean.JournalDossierForm.COMMENT_PARAM;
import static fr.dila.ss.ui.th.bean.JournalDossierForm.DATE_PARAM;
import static fr.dila.ss.ui.th.bean.JournalDossierForm.POSTE_PARAM;
import static fr.dila.ss.ui.th.bean.JournalDossierForm.USER_PARAM;

import fr.dila.ss.ui.th.bean.JournalDossierForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class JournalDossierResultList {
    protected final List<ColonneInfo> listeColonnes = new ArrayList<>();
    protected List<? extends JournalDossierListingDTO> liste = new ArrayList<>();
    protected Integer nbTotal = 0;

    public JournalDossierResultList() {
        super();
    }

    public List<? extends JournalDossierListingDTO> getListe() {
        return liste;
    }

    public void setListe(List<? extends JournalDossierListingDTO> liste) {
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

    public List<ColonneInfo> getListeColonnes(JournalDossierForm form) {
        buildColonnes(form);
        return listeColonnes;
    }

    protected void buildColonnes(JournalDossierForm form) {
        listeColonnes.clear();
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.content.header.date", true, true, false, true)
                .sortName(DATE_PARAM)
                .sortId(getSortId(DATE_PARAM))
                .sortValue(form == null ? null : form.getDate())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.utilisateur", true, true, false, true)
                .sortName(USER_PARAM)
                .sortId(getSortId(USER_PARAM))
                .sortValue(form == null ? null : form.getUtilisateur())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.content.header.poste", true, true, false, true)
                .sortName(POSTE_PARAM)
                .sortId(getSortId(POSTE_PARAM))
                .sortValue(form == null ? null : form.getPoste())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.content.header.categorie", true, true, false, true)
                .sortName(CATEGORY_PARAM)
                .sortId(getSortId(CATEGORY_PARAM))
                .sortValue(form == null ? null : form.getCategorie())
                .build()
        );
        listeColonnes.add(
            ColonneInfo
                .builder("journal.label.content.header.commentaire", true, true, false, true)
                .sortName(COMMENT_PARAM)
                .sortId(getSortId(COMMENT_PARAM))
                .sortValue(form == null ? null : form.getCommentaire())
                .build()
        );
    }

    protected String getSortId(String columnName) {
        return columnName + "Header";
    }
}
