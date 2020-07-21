package fr.dila.solonepp.page.communication.sd.detail.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.create.generique.CreateCommGenerique12Page;

public class DetailCommGenerique12Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommGenerique12Page.class;
    }
}
