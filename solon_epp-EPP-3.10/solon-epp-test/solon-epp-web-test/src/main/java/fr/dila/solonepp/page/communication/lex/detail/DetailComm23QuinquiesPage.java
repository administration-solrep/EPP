package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23QuinquiesPage;

public class DetailComm23QuinquiesPage extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-30 : Enregistrement de la demande de lecture définitive";
    public static final String DATE_DEPOT_TEXTE = "nxw_metadonnees_version_dateDepotTexte_row";
    public static final String NUMERO_DEPOT_TEXTE = "nxw_metadonnees_version_numeroDepotTexte_row";
    public static final String COMMISSION_SAISIE_FOND = "nxw_metadonnees_version_commissionSaisieAuFond_row";
    public static final String COMMISSION_SAISIE_AVIS = "nxw_metadonnees_version_commissionSaisiePourAvis_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm23QuinquiesPage.class;
    }
}
