package fr.dila.solonepp.page.communication.sd.create.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.detail.generique.DetailCommGenerique12Page;

public class CreateCommGenerique12Page extends AbstractCreateComm {

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommGenerique12Page.class;

    }
}