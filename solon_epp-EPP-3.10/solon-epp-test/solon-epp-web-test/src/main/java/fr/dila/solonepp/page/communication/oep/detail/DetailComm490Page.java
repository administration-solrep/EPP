package fr.dila.solonepp.page.communication.oep.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.oep.create.CreateComm490Page;


public class DetailComm490Page extends AbstractDetailComm {


    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm490Page.class;
    }

}
