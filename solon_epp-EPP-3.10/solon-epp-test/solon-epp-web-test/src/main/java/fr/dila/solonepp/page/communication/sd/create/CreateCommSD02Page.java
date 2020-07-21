package fr.dila.solonepp.page.communication.sd.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.detail.DetailCommSD02Page;

public class CreateCommSD02Page extends AbstractCreateComm {

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommSD02Page.class;

    }
}