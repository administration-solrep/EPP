package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm28Page;

public class DetailComm28Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-35 : Publication";
    public static final String TITRE = "nxw_metadonnees_version_titre_row";
    public static final String DATE_PROMULGATION = "nxw_metadonnees_version_datePromulgation_row";
    public static final String DATE_PUBLICATION = "nxw_metadonnees_version_datePublication_row";
    public static final String NUMERO_LOI = "nxw_metadonnees_version_numeroLoi_row";
    public static final String NUMERO_JO = "nxw_metadonnees_version_numeroJo_row";
    public static final String PAGE_JO = "nxw_metadonnees_version_pageJo_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm28Page.class;
    }
}
