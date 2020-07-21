package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm04BisPage;

public class DetailComm04BisPage extends AbstractDetailComm {

    public static final String VERSION_TITRE = "nxw_metadonnees_version_titre_row";
    public static final String DATE_DISTRIBUTION_ELECTRONIQUE = "nxw_metadonnees_version_dateDistributionElectronique_row";
    public static final String NATURE_RAPPORT = "nxw_metadonnees_version_natureRapport_row";
    public static final String DATE_DEPOT_RAPPORT = "nxw_metadonnees_version_dateDepotRapport_row";
    public static final String DATE_DEPOT_TEXTE = "nxw_metadonnees_version_dateDepotTexte_row";
    public static final String NUMERO_DEPOT_RAPPORT = "nxw_metadonnees_version_numeroDepotRapport_row";
    public static final String NUMERO_DEPOT_TEXTE = "nxw_metadonnees_version_numeroDepotTexte_row";
    public static final String COMMISSION_SAISIE = "nxw_metadonnees_version_commissionSaisie_row";
    public static final String RAPPORTEURS_LIST = "nxw_metadonnees_version_rapporteurList_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm04BisPage.class;
    }
}
