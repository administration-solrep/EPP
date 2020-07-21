package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm21Page;

public class DetailComm21Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-24 : CMP - Convocation";    
    public static final String DATE_CONVOCATION = "nxw_metadonnees_version_dateList_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm21Page.class;
    }
}
