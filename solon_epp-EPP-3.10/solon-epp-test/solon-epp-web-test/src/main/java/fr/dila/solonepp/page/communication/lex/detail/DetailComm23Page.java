package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23Page;

public class DetailComm23Page extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-26 : CMP - Demande de lecture des conclusions";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm23Page.class;
    }
}
