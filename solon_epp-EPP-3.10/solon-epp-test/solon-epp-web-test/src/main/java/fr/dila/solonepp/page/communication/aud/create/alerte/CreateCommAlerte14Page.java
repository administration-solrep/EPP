package fr.dila.solonepp.page.communication.aud.create.alerte;

import fr.dila.solonepp.page.AlertePage;
import fr.dila.solonepp.page.communication.aud.detail.alerte.DetailCommAlerte14Page;

public class CreateCommAlerte14Page extends AlertePage {

    public static final String TYPE_COMM = "Alerte - Demandes d'audition";
    public static final String COMMUNICATION = "nxw_metadonnees_evenement_libelle";

//    @Override
//    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
//        return DetailCommAlerte14Page.class;
//    }

    public DetailCommAlerte14Page createCommAlerte14Page(String destinataire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        publier();
        return getPage(DetailCommAlerte14Page.class);
    }
    
    public CreateCommAlerte14Page failCreateCommAlerte14Page(String destinataire, String expectedMessageFail) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        publier(expectedMessageFail);
        return getPage(CreateCommAlerte14Page.class);
    }

}