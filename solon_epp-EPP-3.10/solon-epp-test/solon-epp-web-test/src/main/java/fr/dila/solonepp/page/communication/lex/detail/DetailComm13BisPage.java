package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm13BisPage;

public class DetailComm13BisPage extends AbstractDetailComm {

    public static final String NIVEAU_LECTURE = "nxw_metadonnees_version_niveauLecture_row";
    public static final String NUMERO_TEXTE_ADOPTE = "nxw_metadonnees_version_numeroTexteAdopte_row";
    public static final String DATE_ADOPTION = "nxw_metadonnees_version_dateAdoption_row";
    public static final String SORT_ADOPTION = "nxw_metadonnees_version_sortAdoption_row";
    public static final String DATE_DEPOT = "nxw_metadonnees_version_dateDepotTexte_row";
    public static final String NUMERO_DEPOT = "nxw_metadonnees_version_numeroDepotTexte_row";
    public static final String COMM_SAISIE_FOND = "nxw_metadonnees_version_commissionSaisieAuFond_row";
    public static final String COMM_SAISIE_AVIS = "nxw_metadonnees_version_commissionSaisiePourAvis_row";
    
    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm13BisPage.class;
    }
}
