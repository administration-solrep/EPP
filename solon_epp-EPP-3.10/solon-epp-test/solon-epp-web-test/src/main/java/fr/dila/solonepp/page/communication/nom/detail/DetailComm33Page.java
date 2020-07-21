package fr.dila.solonepp.page.communication.nom.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.nom.create.CreateComm34Page;

public class DetailComm33Page extends AbstractDetailComm{

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm34Page.class;
    }

}
