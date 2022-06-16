package fr.dila.epp.ui.bean;

import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import java.util.ArrayList;
import java.util.List;

public class MessageList {
    private final List<ColonneInfo> listeColones = new ArrayList<>();

    private List<EppMessageDTO> liste = new ArrayList<>();

    private static final String COLUMN_ID_DOSSIER = "idDossierTri";
    private static final String COLUMN_OBJET_DOSSIER = "objetDossierTri";
    private static final String COLUMN_LECTURE = "lecture";
    private static final String COLUMN_EMETTEUR = "emetteurTri";
    private static final String COLUMN_DESTINATAIRE = "destinataireTri";
    private static final String COLUMN_COPIE = "copieTri";
    private static final String COLUMN_COMMUNICATION = "communicationTri";
    private static final String COLUMN_DATE = "dateTri";

    private Integer nbTotal;

    private String titre;

    private String sousTitre;

    public MessageList() {
        this.nbTotal = 0;
    }

    public List<EppMessageDTO> getListe() {
        return liste;
    }

    public void setListe(List<EppMessageDTO> liste) {
        this.liste = liste;
    }

    public Integer getNbTotal() {
        return nbTotal;
    }

    public void setNbTotal(Integer nbTotal) {
        this.nbTotal = nbTotal;
    }

    public List<ColonneInfo> getListeColones() {
        return listeColones;
    }

    public List<ColonneInfo> getListeColones(MessageListForm form) {
        buildColonnes(form);
        return listeColones;
    }

    private void buildColonnes(MessageListForm form) {
        listeColones.clear();

        if (form != null) {
            listeColones.add(new ColonneInfo(null, false, true, false, true));
            listeColones.add(
                new ColonneInfo(
                    "corbeille.communication.table.header.iddossier",
                    true,
                    COLUMN_ID_DOSSIER,
                    form.getIdDossier()
                )
            );
            listeColones.add(
                new ColonneInfo(
                    "corbeille.communication.table.header.objetdossier",
                    true,
                    COLUMN_OBJET_DOSSIER,
                    form.getObjetDossier()
                )
            );
            listeColones.add(
                new ColonneInfo("corbeille.communication.table.header.lecture", false, COLUMN_LECTURE, null)
            );
            listeColones.add(
                new ColonneInfo(
                    "corbeille.communication.table.header.emeteur",
                    true,
                    COLUMN_EMETTEUR,
                    form.getEmetteur()
                )
            );
            listeColones.add(
                new ColonneInfo(
                    "corbeille.communication.table.header.destinataire",
                    true,
                    COLUMN_DESTINATAIRE,
                    form.getDestinataire()
                )
            );
            listeColones.add(
                new ColonneInfo("corbeille.communication.table.header.copie", true, COLUMN_COPIE, form.getCopie())
            );
            listeColones.add(
                new ColonneInfo(
                    "corbeille.communication.table.header.communication",
                    true,
                    COLUMN_COMMUNICATION,
                    form.getCommunication()
                )
            );
            listeColones.add(new ColonneInfo("corbeille.communication.table.header.version", false, COLUMN_DATE, null));
            listeColones.add(
                new ColonneInfo("corbeille.communication.table.header.date", true, COLUMN_DATE, form.getDate())
            );
        }
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
