package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm10Page;

public class DetailComm10Page extends AbstractDetailComm {
	
    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm10Page.class;
    }

}
