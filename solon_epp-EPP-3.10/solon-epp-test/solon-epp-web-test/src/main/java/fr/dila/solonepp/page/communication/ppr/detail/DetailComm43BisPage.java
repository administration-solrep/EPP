package fr.dila.solonepp.page.communication.ppr.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;

public class DetailComm43BisPage extends AbstractDetailComm {

    public static final String AUTEUR = "nxw_metadonnees_evenement_auteur";
    public static final String OBJET = "meta_evt:nxl_metadonnees_evenement:nxw_metadonnees_evenement_objet";
    public static final String DATE_DEPOT_TEXTE = "meta_evt:nxl_metadonnees_evenement:nxw_metadonnees_evenement_dateDepotTexte";
    public static final String NUMERO_DEPOT_TEXTE = "meta_evt:nxl_metadonnees_evenement:nxw_metadonnees_evenement_numeroDepotTexte ";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return null;
    }

}
