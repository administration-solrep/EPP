package fr.dila.ss.ui.bean.actualites;

import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class ActualitesList {
    private final List<ColonneInfo> listeColonnes = new ArrayList<>();

    private List<ActualiteConsultationDTO> liste = new ArrayList<>();

    public static final String COLUMN_SORT_DATE_EMISSION = "dateEmissionSort";
    public static final String COLUMN_SORT_DATE_FIN_VALIDITE = "dateFinValiditeSort";
    public static final String COLUMN_SORT_OBJET = "objetSort";
    public static final String COLUMN_SORT_HASPJ = "hasPjSort";
    public static final String COLUMN_SORT_STATUS = "statutSort";

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public ActualitesList() {
        this.nbTotal = 0;
    }

    public ActualitesList(ActualiteRechercheForm form) {
        this.nbTotal = 0;
        buildColonnes(form);
    }

    public List<ActualiteConsultationDTO> getListe() {
        return liste;
    }

    public void setListe(List<ActualiteConsultationDTO> liste) {
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

    public void buildColonnes(ActualiteRechercheForm form) {
        listeColonnes.clear();

        listeColonnes.add(
            new ColonneInfo.Builder("actualite.content.header.date.emission", true, true, true, true)
                .sortId(COLUMN_SORT_DATE_EMISSION)
                .sortName(COLUMN_SORT_DATE_EMISSION)
                .sortValue(form.getDateEmissionSort())
                .build()
        );

        if (form instanceof ActualiteAdminRechercheForm) {
            buildAdminColonnes((ActualiteAdminRechercheForm) form);
        }

        listeColonnes.add(
            new ColonneInfo.Builder("actualite.content.header.objet", true, true, true, true)
                .sortId(COLUMN_SORT_OBJET)
                .sortName(COLUMN_SORT_OBJET)
                .sortValue(form.getObjetSort())
                .build()
        );

        listeColonnes.add(
            new ColonneInfo.Builder("actualite.content.header.piece.jointe", true, true, true, true)
                .sortId(COLUMN_SORT_HASPJ)
                .sortName(COLUMN_SORT_HASPJ)
                .sortValue(form.getHasPjSort())
                .build()
        );
    }

    private void buildAdminColonnes(ActualiteAdminRechercheForm form) {
        listeColonnes.add(
            new ColonneInfo.Builder("actualite.content.header.date.fin.validite", true, true, true, true)
                .sortId(COLUMN_SORT_DATE_FIN_VALIDITE)
                .sortName(COLUMN_SORT_DATE_FIN_VALIDITE)
                .sortValue(form.getDateFinValiditeSort())
                .build()
        );

        listeColonnes.add(
            new ColonneInfo.Builder("actualite.content.header.statut", true, true, true, true)
                .sortId(COLUMN_SORT_STATUS)
                .sortName(COLUMN_SORT_STATUS)
                .sortValue(form.getStatusSort())
                .build()
        );
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
}
