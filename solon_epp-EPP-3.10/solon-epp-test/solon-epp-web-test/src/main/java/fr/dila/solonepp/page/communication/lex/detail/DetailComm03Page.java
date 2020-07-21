package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm03Page;

public class DetailComm03Page extends AbstractDetailComm {

    public static final String COMM_SAISIE_FOND = "nxw_metadonnees_version_commissionSaisieAuFond_row";
    public static final String COMM_SAISIE_AVIS = "nxw_metadonnees_version_commissionSaisiePourAvis_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm03Page.class;
    }
}
