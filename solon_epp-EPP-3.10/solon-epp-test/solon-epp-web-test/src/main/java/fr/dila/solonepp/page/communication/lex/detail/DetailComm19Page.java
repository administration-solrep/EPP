package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm19Page;

public class DetailComm19Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-22 : CMP - Demande de réunion par le Gouvernement";    
    public static final String DATE_COMM = "nxw_metadonnees_version_date_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm19Page.class;
    }
}
