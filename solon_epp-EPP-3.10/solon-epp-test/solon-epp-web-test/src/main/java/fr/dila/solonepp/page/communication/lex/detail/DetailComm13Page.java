package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm13Page;

public class DetailComm13Page extends AbstractDetailComm {

    public static final String NIVEAU_LECTURE = "nxw_metadonnees_version_niveauLecture_row";
    public static final String NUMERO_TEXTE_ADOPTE = "nxw_metadonnees_version_numeroTexteAdopte_row";
    public static final String DATE_ADOPTION = "nxw_metadonnees_version_dateAdoption_row";
    public static final String SORT_ADOPTION = "nxw_metadonnees_version_sortAdoption_row";
    
    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm13Page.class;
    }
}
