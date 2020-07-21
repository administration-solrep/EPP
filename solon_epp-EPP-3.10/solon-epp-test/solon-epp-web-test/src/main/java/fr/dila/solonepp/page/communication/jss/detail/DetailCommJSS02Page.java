package fr.dila.solonepp.page.communication.jss.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.jss.create.CreateCommJSS02Page;

public class DetailCommJSS02Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommJSS02Page.class;
    }
}
