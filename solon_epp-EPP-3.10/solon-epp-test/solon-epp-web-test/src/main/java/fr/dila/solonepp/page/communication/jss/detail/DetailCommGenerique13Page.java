package fr.dila.solonepp.page.communication.jss.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.jss.create.CreateCommGenerique13Page;


public class DetailCommGenerique13Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommGenerique13Page.class;
    }

}
