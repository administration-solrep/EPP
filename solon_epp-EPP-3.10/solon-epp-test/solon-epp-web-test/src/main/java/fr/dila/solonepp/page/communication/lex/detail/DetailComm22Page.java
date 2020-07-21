package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm22Page;

public class DetailComm22Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-25 : CMP - Notification du résultat et dépôt du rapport";
    public static final String DATE_DEPOT = "nxw_metadonnees_version_dateDepotTexte_row";
    public static final String NUMERO_DEPOT_RAPPORT = "nxw_metadonnees_version_numeroDepotRapport_row";
    public static final String NUMERO_DEPOT_TEXTE = "nxw_metadonnees_version_numeroDepotTexte_row";
    public static final String RESULTAT_CMP = "nxw_metadonnees_version_resultatCMP_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm22Page.class;
    }
}
