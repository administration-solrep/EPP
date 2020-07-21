package fr.dila.solonepp.page.communication.sd.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.create.CreateCommSD02Page;

public class DetailCommSD02Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommSD02Page.class;
    }

}
