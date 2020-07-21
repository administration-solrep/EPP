package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm24Page;

public class DetailComm24Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-31 : Adoption définitive / Adoption ou rejet en Congrès";
    public static final String DATE_ADOPTION = "nxw_metadonnees_version_dateAdoption_row";
    public static final String NUMERO_TEXTE_ADOPTE = "nxw_metadonnees_version_numeroTexteAdopte_row";
    public static final String SORT_ADOPTION = "nxw_metadonnees_version_sortAdoption_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm24Page.class;
    }
}
