package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23TerPage;

public class DetailComm23TerPage extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-28 : Enregistrement de la demande de nouvelle lecture";
    public static final String DATE_DEPOT = "nxw_metadonnees_version_dateDepotTexte_row";
    public static final String NUMERO_DEPOT = "nxw_metadonnees_version_numeroDepotTexte_row";
    public static final String COMMISSION_SAISIE_FOND = "nxw_metadonnees_version_commissionSaisieAuFond_row";
    public static final String COMMISSION_SAISIE_AVIS = "nxw_metadonnees_version_commissionSaisiePourAvis_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm23TerPage.class;
    }
}
